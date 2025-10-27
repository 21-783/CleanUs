package com.example.cleanus_mockapi.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TransactionItem(
      String providerTxId,
      String txAt,
      String shopName,
      String amount
) {}
