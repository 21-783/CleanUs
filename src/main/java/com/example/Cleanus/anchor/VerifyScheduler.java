package com.example.Cleanus.anchor;

import com.example.Cleanus.batch.Batch;
import com.example.Cleanus.batch.BatchRepository;
import com.example.Cleanus.batch.BatchStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class VerifyScheduler {

    private final BatchRepository batchRepo;
    private final VerificationService verificationService;

    @Scheduled(cron = "${cleanus.anchor.verify.cron:0 * * * * *}")
    public void verifyVerifyingBatches() {
        var verifying = batchRepo.findByStatus(BatchStatus.VERIFYING);
        for (Batch b : verifying) {
            try {
                verificationService.verifyOnce(b);
            } catch (Exception e) {
                log.error("Verify scheduler error. batchId={}, txHahs={}", b.getId(), b.getOnchainTxHash(), e);

            }
        }
    }
}
