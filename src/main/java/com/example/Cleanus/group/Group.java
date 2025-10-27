package com.example.Cleanus.group;


import com.example.Cleanus.ledger.Ledger;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "groups",
        indexes = {@Index(name = "idx_group_owner", columnList = "owner_user_id")})
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "password_hash", nullable = false, length = 200)
    private String passwordHash;

    @Column(name = "owner_user_id", nullable = false)
    private Long ownerUserId;

    @OneToOne (mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Ledger ledger;

    @Column(nullable = false, updatable = false)
    private Instant createAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

}
