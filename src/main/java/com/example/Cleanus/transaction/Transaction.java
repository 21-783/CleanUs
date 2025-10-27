package com.example.Cleanus.transaction;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions",
        // 자주 조회되는 컬럼 조합에 관한 인덱스를 생성하여, 조회 성능 향상을 돕는다.
        indexes = {
                // 특정 장부("ledger_id")의 날짜별("tx_at") 거래 내역 조회
                @Index(name="idx_tx_ledger_dt", columnList = "ledger_id, tx_at"),
                // Mock API로부터 받은 거래 고유키('provider_tx_id")를 기준으로 조회
                @Index(name="idx_tx_provider", columnList = "provider_tx_id")
        },
        // "ledger_id + provider_tx_id" 조합이 유일해야 한다는 의미.
        uniqueConstraints = @UniqueConstraint(
                name ="uk_ledger_provider",
                columnNames = {"ledger_id", "provider_tx_id"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="ledger_id", nullable = false)
    private Long ledgerId;

    @Column(name="fintech_use_num", length=64, nullable = false)
    private String fintechUseNum;

    @Column(name="provider_tx_id", length=64, nullable = false)
    private String providerTxId;

    @Column(name ="tx_at", nullable = false)
    private Instant txAt;

    @Column(name ="shop_name", nullable = false, length = 200)
    private String shopName;

    @Column(name="amount", nullable = false, precision=19, scale = 2)
    private BigDecimal amount;

    @Column(name="leaf_hash", length=66)
    private String leafHash;
}
