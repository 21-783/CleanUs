package com.example.Cleanus.anchor.dto;

// 앵커링 요청에 따른 응답 데이터.
public record AnchorResponse(String batchId, String merkleRoot, String onchainTxHash, Long blockNumber) {}
