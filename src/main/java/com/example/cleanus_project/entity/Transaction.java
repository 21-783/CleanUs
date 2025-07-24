package com.example.cleanus_project.entity;

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
        if (withdrawAmount != null && !withdrawAmount.equals("0원")) {
            return withdrawAmount;
        } else if (depositAmount != null) {
            return depositAmount;
        } else {
            return "0원"; // 둘 다 null일 경우를 대비한 디폴트
        }
    }
}