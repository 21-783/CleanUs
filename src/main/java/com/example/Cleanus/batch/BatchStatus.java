package com.example.Cleanus.batch;

public enum BatchStatus {
    PENDING,    // Batch 봉인 전 (거래 수집 중)
    SEALED,     // Batch 봉인 완료 (Merkle Root 계산 완료)
    ANCHORING,  // 블록체인 앵커링 요청 중
    VERIFYING,  // 앵커링 성공 후 검증 중
    ANCHORED,   // 최종 블록체인 앵커링 완료
    FAILED      // 실패 (앵커링 또는 검증 실패)
}
