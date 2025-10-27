package com.example.cleanus_mockapi.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByStatus(Account.Status status);
}
