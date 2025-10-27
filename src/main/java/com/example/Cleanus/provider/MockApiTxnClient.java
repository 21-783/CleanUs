package com.example.Cleanus.provider;

import com.example.Cleanus.provider.dto.TransactionListRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

// 외부 Mock API에 HTTP GET 요청을 보내 거래내역을 받아오는 클라이언트 역할
@Component @RequiredArgsConstructor
public class MockApiTxnClient {
    private final WebClient mockApiClient;

    public TransactionListRes fetchTransactions(String fintechUseNum, String fromIso, String toIso) {
        return mockApiClient.get()
                .uri(u -> u.path("/transactions")
                        .queryParam("fintechUseNum", fintechUseNum)
                        .queryParam("from", fromIso)
                        .queryParam("to", toIso)
                        .build())
                .retrieve()
                .bodyToMono(TransactionListRes.class).block();
    }

}
