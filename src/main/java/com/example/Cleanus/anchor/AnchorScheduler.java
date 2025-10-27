package com.example.Cleanus.anchor;

import com.example.Cleanus.batch.BatchRepository;
import com.example.Cleanus.batch.BatchStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnchorScheduler {

    private final BatchRepository batchRepo;
    private final AnchoringService anchoringService;

    @Scheduled(cron="${cleanus.anchor.run.cron:0 */10 * * * *}")
    public void runAnchoringForSealedBatches() {
        var sealed = batchRepo.findByStatus(BatchStatus.SEALED);
        for (var b : sealed) {
            try {
                anchoringService.anchorBatch(b);
            } catch (Exception e) {
                log.error("Anchoring scheduler error. batchId={}", b.getId(), e);
            }
        }
    }
}
