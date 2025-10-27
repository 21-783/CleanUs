package com.example.cleanus_mockapi.api.dto;

import java.util.List;

public record TransactionListRes(
        String fintechUseNum,
        List<TransactionItem> items
) {}
