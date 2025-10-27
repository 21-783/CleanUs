package com.example.Cleanus.batch;

import com.example.Cleanus.ledger.LedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// 스케쥴러를 이용하여, 마지막 봉인된 배치 이후 새 거래가 봉인 조건에 부합하면, 자동으로 봉인(SEALED)상태로 저장하는 역할.
@Component
@RequiredArgsConstructor
public class BatchSealScheduler {

    private final LedgerRepository ledgerRepo;
    private final BatchSealService sealService;

    @Scheduled(cron="${cleanus.batch.seal.cron:0 0 2 * * *}")
    public void sealAllLedgers() {
        ledgerRepo.findAll().forEach(l -> sealService.trySealForLedger(l.getId()));
    }
}
