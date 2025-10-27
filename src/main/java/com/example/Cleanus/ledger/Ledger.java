package com.example.Cleanus.ledger;

import com.example.Cleanus.group.Group;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ledgers",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"group_id"})})
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ledger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "group_id", nullable = false, unique = true)
    private Group group;

    @Column(length = 200)
    private String title;
}
