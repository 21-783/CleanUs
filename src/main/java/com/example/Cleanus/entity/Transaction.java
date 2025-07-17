package com.example.Cleanus.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bankName;
    private String accountNumber;
    private String date;
    private String shopName;
    private String withdrawAmount;
    private String depositAmount;

    public String getAmount() {
        return !withdrawAmount.equals("0원") ? withdrawAmount : depositAmount;
    }
}