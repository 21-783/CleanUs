package com.example.Cleanus.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

// Main server <> Node.js server 간 HTTP 통신을 담당하는 클라이언트를 설정하는 역할.
@Configuration
@RequiredArgsConstructor
public class NodeClientConfig {

    @Bean
    WebClient nodeClient(
            @Value("${cleanus.anchor.node.base-url}") String baseUrl,
            @Value("${cleanus.node.api-key}") String apiKey,
            @Value("${cleanus.anchor.node.connect-timeout-ms}") int connectTimeoutMs,
            @Value("${cleanus.anchor.node.read-timeout-sec}") int readTimeoutSec
    ){
        HttpClient http = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                .responseTimeout(Duration.ofSeconds(readTimeoutSec))
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(readTimeoutSec, TimeUnit.SECONDS)));

        ExchangeFilterFunction requestedFilter = (request, next) -> {
            var headers = request.headers();
            if (!headers.containsKey("X-Request-Id")) {
                headers.add("X-Request-Id", UUID.randomUUID().toString());
            }
            return next.exchange(request);
        };

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(http))
                .defaultHeader("X-Api-Key", apiKey)
                .defaultHeader("Content-Type", "application/json")
                .filter(requestedFilter)
                .build();
    }
}
