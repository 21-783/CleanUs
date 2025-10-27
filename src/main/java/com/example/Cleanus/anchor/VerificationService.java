package com.example.Cleanus.anchor;

import com.example.Cleanus.batch.Batch;
import com.example.Cleanus.batch.BatchRepository;
import com.example.Cleanus.batch.BatchStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import java.io.IOException;

// Node.js가 블록체인에 기록한 트랜잭션의 결과를 직접 검증하는 서비스를 담당하는 파일.
@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationService {

    private final Web3j web3j;
    private final BatchRepository batchRepo;

    // txReceipt 존재를 확인하고 성공 / 실패 여부를 확인하여 (성공 > ANCHORED, 실패 > FAILED) 로 상태 변경
    // txReceipt가 아예 존재하지 않는다면 상태(VERIFYING)을 그대로 유지 후, 다음 주기에 재확인한다.
    public Batch verifyOnce(Batch batch) throws IOException {
        var txHash = batch.getOnchainTxHash();
        var receiptOpt = web3j.ethGetTransactionReceipt(txHash).send().getTransactionReceipt();

        if (receiptOpt.isEmpty()) {
            log.info("Tx not yet included. batchId={}, txHash={}", batch.getId(), txHash);
            return batch;
        }

        TransactionReceipt r = receiptOpt.get();

        boolean success = "0x1".equalsIgnoreCase(r.getStatus());
        if(success) {
            batch.setStatus(BatchStatus.ANCHORED);
            batch.setVerifiedAt(java.time.Instant.now());
        } else {
            batch.setStatus(BatchStatus.FAILED);
        }
        return batchRepo.save(batch);
    }
}
