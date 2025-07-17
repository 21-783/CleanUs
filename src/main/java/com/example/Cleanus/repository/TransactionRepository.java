package com.example.Cleanus.repository;

import com.example.Cleanus.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsByDateAndShopNameAndAmount(String date, String shopName, String amount);
}