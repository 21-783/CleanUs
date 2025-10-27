package com.example.Cleanus.batch;

import com.example.Cleanus.transaction.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;

// 최근에 마지막으로 봉인된 배치 이후로 들어온 거래들을 모아, 봉인 조건(시간, 개수)에 부합하는
// 거래 데이터들을 하나의 Batch 형태로 봉인해서 `batches` 테이블에 저장.

@Service
@RequiredArgsConstructor
public class BatchSealService {

    private final TransactionRepository txRepo;
    private final BatchRepository batchRepo;

    // 봉인(Seal) 조건
    @Value("${cleanus.batch.size-threshold:50}") int sizeThreshold;
    @Value("${cleanus.batch.time-days:14}") int timeThresholdDays;

    @Transactional
    public Optional<Batch> trySealForLedger(Long ledgerid) {
        // Instant.EPOCH 의 사용 이유 : 아직 봉인된 배치가 없는 서비스 초기일 때를 대비한 부분.
        // EPOCH : 시간의 초기값을 의미 (UTC 1970-01-01T00:00:00Z)
        var from = batchRepo.findTopByLedgerIdOrderByAtDesc(ledgerid)
                .map(Batch::getToAt).orElse(Instant.EPOCH);
        var to = Instant.now();

        var txs = txRepo.findByLedgerIdAndTxAtBetween(ledgerid, from, to);
        if (txs.isEmpty()) return Optional.empty();

        // 일정한 해시를 위한 정렬 규칙 적용 ( 1순위:거래시각 / 2순위:고유ID / 3순위:거래금액)
        txs.sort(Comparator
                .comparing(com.example.Cleanus.transaction.Transaction::getTxAt)
                .thenComparing(com.example.Cleanus.transaction.Transaction::getProviderTxId)
                .thenComparing(t -> t.getAmount()));

        boolean sizeOK = txs.size() >= sizeThreshold;
        boolean timeOK = Duration.between(from, to).toDays() >= timeThresholdDays;
        if(!(sizeOK || timeOK)) return Optional.empty();

        var b = Batch.builder()
                .ledgerId(ledgerid)
                .fromAt(from)
                .toAt(to)
                .txCount(txs.size())
                .status(BatchStatus.SEALED)
                .sealedAt(Instant.now())
                .build();

        return Optional.of(batchRepo.save(b));
    }
}
