package com.example.cleanus_mockapi.transaction;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @Column(name = "provider_tx_id", length = 80, nullable = false)
    private String providerTxId;

    @Column(name = "fintech_use_num", length = 80, nullable = false)
    private String fintechUseNum;

    @Column(name = "tx_at", nullable = false)
    private Instant txAt;

    @Column(name = "shop_name", length = 200, nullable = false)
    private String shopName;

    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "balance_after", precision = 19, scale = 2, nullable = false)
    private BigDecimal balanceAfter;

}
