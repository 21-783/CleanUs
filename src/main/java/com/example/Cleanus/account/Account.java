package com.example.Cleanus.account;

import com.example.Cleanus.ledger.Ledger;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "accounts",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"ledger_id"})
        },
        indexes = {
            @Index(name = "idx_acc_ledger", columnList = "ledger_id")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ledger_id", nullable = false, unique = true)
    private Ledger ledger;

    @NotBlank
    @Column(name = "bank_name", nullable = false, length=80)
    private String bankName;

    @Column(name= "account_number_hash", nullable =false, length = 128)
    private String accountNumberHash;

    @Column(name = "account_last4", nullable = false, length=8)
    private String accountLast4;

    @Column(name = "display_name", nullable = false,length=120)
    private String displayName;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
