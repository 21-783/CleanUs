package com.example.Cleanus.batch;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name="batches",
    // BatchSealService.java에서 새로운 Batch를 봉인 전, 가장 최근 봉인 시점 이후의 거래들만 다시 수집하기 위해 사용.
    // BatchSealScheduler.java에서 '마지막 봉인 시점 이후 새 거래가 있는지'확인 할 때 사용.
    // AnchoringService.java에서 장부(Ledger)별로 시간 순 Batch 리스트를 조회할 때 사용.
    indexes = @Index(name="idx_batch_ledger_from", columnList="ledger_id, from_at"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Batch {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="ledger_id", nullable = false) private Long ledgerId;
    @Column(name="from_at", nullable = false)   private Instant fromAt;
    @Column(name="to_at",  nullable = false)    private Instant toAt;
    @Column(name="tx_count", nullable = false)  private Integer txCount;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false) private BatchStatus status;

    @Column(name="merkle_root", length=66)      private String merkleRoot;
    @Column(name="onchain_tx_hash", length=66)  private String onchainTxHash;
    @Column(name="block_number")                private Long blockNumber;

    @Column(name="sealed_at")   private Instant sealedAt;
    @Column(name="anchored_at") private Instant anchoredAt;
    @Column(name="verified_at") private Instant verifiedAt;
}
