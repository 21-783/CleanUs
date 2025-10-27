package com.example.Cleanus.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.*;


import java.util.UUID;

/* 이 파일의 역할
 * 전역 WebClient 관리. (Mock API 용 / Node Server 용)
 * X-Reqeust-Id 관리. (누락 시, 추가)
 * 스케쥴러 활성화 지점. (@EnableScheduling)
 */

@Configuration
@EnableScheduling // 스케쥴러를 앞으로 사용하겠다는 사전 설정.
@RequiredArgsConstructor
public class ApiClientsConfig {

    // @Value : application.properties 로부터 값을 주입받아 온다.
    @Value("${cleanus.mock.base-url") String mockBaseUrl;
    @Value("${cleanus.mock.api-key") String mockApiKey;

    @Value("${cleanus.node.base-url") String nodeBaseUrl;
    @Value("${cleanus.node.api-key") String nodeApiKey;

    // WebClient 요청에 'X-Request-Id' 헤더가 누락되어 있다면, 강제 주입하는 필터.
    private ExchangeFilterFunction requestIdFilter() {
        return (req, next) -> {
            var h = req.headers();
            if (!h.containsKey("X-Reqeust-Id")) h.add("X-Request-Id", UUID.randomUUID().toString());
            return next.exchange(req);
        };
    }

    // Mock API 전용 WebClient Bean.
    @Bean WebClient mockApiClient(WebClient.Builder b) {
        return b.baseUrl(mockBaseUrl)
                .defaultHeader("X-Api-key", mockApiKey)
                .defaultHeader("Content-Type", "application/json")
                .filter(requestIdFilter())
                .build();
    }

    // Node Server 전용 WebClient Bean.
    @Bean WebClient nodeClient(WebClient.Builder b) {
        return b.baseUrl(nodeBaseUrl)
                .defaultHeader("X-Api-key", nodeApiKey)
                .defaultHeader("Content-type", "application/json")
                .filter(requestIdFilter())
                .build();
    }

}
