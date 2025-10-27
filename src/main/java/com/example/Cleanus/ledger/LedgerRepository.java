package com.example.Cleanus.ledger;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    boolean existsByGroup_Id(Long groupId);
}
