package com.example.Cleanus.anchor;

import com.example.Cleanus.anchor.dto.AnchorRequest;
import com.example.Cleanus.anchor.dto.AnchorResponse;
import com.example.Cleanus.batch.Batch;
import com.example.Cleanus.batch.BatchRepository;
import com.example.Cleanus.batch.BatchStatus;
import com.example.Cleanus.merkle.Merkle;
import com.example.Cleanus.transaction.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;

// SEALED 상태의 Batch를 Node.js server에 보내어 앵커링하고, 결과를 DB에 업데이트하는 역할.
@Service
@RequiredArgsConstructor
@Slf4j
public class AnchoringService {

    private final AnchoringClient client;
    private final BatchRepository batchRepo;
    private final TransactionRepository txRepo;

    @Transactional
    public Batch anchorBatch(Batch sealed){
        if(sealed.getStatus() != BatchStatus.SEALED)
            throw new IllegalStateException("이 배치는 봉인되지 않았습니다.");
        if(sealed.getOnchainTxHash() != null)
            throw new IllegalStateException("이미 앵커링 시도된 배치입니다.");

        //
        var txs = txRepo.findByLedgerIdAndTxAtBetween(sealed.getLedgerId(), sealed.getFromAt(), sealed.getToAt());
        if (txs.isEmpty()) throw new IllegalStateException("배치 구간에 거래가 없습니다.");

        txs.sort(Comparator
                .comparing(com.example.Cleanus.transaction.Transaction::getTxAt)
                .thenComparing(com.example.Cleanus.transaction.Transaction::getProviderTxId)
                .thenComparing(t -> t.getAmount()));

        var leaves = txs.stream()
                .map(t -> {
                    if(t.getLeafHash() == null) throw new IllegalStateException("leafHash 누락 트랜잭션 존재");
                    return t.getLeafHash();
                })
                .toList();

        if (leaves.isEmpty()) throw new IllegalStateException("앵커링할 leaf가 없습니다.");
        String localRoot = Merkle.root(leaves);

        sealed.setStatus(BatchStatus.ANCHORING);
        batchRepo.save(sealed);

        try {
            AnchorResponse resp = client.anchor(new AnchorRequest(String.valueOf(sealed.getId()), leaves));

            if(resp.merkleRoot() == null || !resp.merkleRoot().equalsIgnoreCase(localRoot)) {
                sealed.setStatus(BatchStatus.FAILED);
                return batchRepo.save(sealed);
            }

            sealed.setMerkleRoot(resp.merkleRoot());
            sealed.setOnchainTxHash(resp.onchainTxHash());
            sealed.setBlockNumber(resp.blockNumber());
            sealed.setAnchoredAt(Instant.now());
            sealed.setStatus(BatchStatus.VERIFYING);
            return batchRepo.save(sealed);
        } catch (IllegalArgumentException e) {
            log.error("Anchoring hard failure (bad request). batchId={}, msg={}", sealed.getId(), e.getMessage(), e);
            sealed.setStatus(BatchStatus.FAILED);
            return  batchRepo.save(sealed);
        } catch (Exception e) {
            log.error("Anchoring transient failure. batchId={}", sealed.getId(), e);
            return sealed;
        }
    }
}
