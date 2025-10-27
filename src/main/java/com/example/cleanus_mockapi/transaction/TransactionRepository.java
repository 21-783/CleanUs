package com.example.cleanus_mockapi.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByFintechUseNumAndTxAtBetweenOrderByTxAtAsc(
            String fintechUseNum, Instant from, Instant to);

    Optional<Transaction> findTopByFintechUseNumOrderByTxAtDesc(String fintechUseNum);
}
