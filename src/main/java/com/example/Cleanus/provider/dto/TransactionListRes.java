package com.example.Cleanus.provider.dto;

import java.util.List;

// Mock API에서 `/transactions`로 요청했을때의 전체 응답 객체
public record TransactionListRes(String fintechUseNum, List<TransacitonItem> items) {
}
