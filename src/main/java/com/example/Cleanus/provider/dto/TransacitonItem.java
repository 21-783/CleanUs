package com.example.Cleanus.provider.dto;

// Mock API에서 `/transactions`로 요청했을때의 응답(TransactionListRes) 안에 포함된 '한 건의 거래'
public record TransacitonItem(String providerTxId, String txAt, String shopName, String amount) {}
