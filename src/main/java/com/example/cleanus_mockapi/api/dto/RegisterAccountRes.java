package com.example.cleanus_mockapi.api.dto;

public record RegisterAccountRes(
        String fintechUseNum,
        String bankCode,
        String accountLast4,
        String status,
        String registeredAt
) {}