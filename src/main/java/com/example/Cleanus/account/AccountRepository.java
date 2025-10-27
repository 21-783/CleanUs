package com.example.Cleanus.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByLedger_Id(Long ledgerId);
    boolean existsByLedger_Id(Long ledgerId);
}
