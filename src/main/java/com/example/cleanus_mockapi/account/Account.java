package com.example.cleanus_mockapi.account;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity @Table(name = "accounts")
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Account {
    @Id
    @Column(name = "fintech_use_num", length = 80, nullable = false)
    private String fintechUseNum;

    @Column(name = "bank_code", length = 10, nullable = false)
    private String bankCode;

    @Column(name = "account_number_hash", length = 128, nullable = false)
    private String accountNumberHash;

    @Column(name = "account_last4", length = 4, nullable = false)
    private String accountLast4;

    @Column(name = "initial_balance", nullable = false)
    private Long initialBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10, nullable = false)
    private Status status;

    @Column(name = "registered_at", nullable = false)
    private Instant registeredAt;

    public enum Status { ACTIVE, INACTIVE }
}
