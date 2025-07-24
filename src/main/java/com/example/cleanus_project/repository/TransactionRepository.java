package com.example.cleanus_project.repository;

import com.example.cleanus_project.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsByDateAndShopNameAndWithdrawAmount(String date, String shopName, String amount);
}