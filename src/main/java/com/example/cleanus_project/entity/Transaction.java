package com.example.cleanus_project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
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
    private Integer amount;
    private Integer groupNum;
}