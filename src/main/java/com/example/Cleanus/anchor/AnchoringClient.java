package com.example.Cleanus.anchor;

import com.example.Cleanus.anchor.dto.AnchorRequest;
import com.example.Cleanus.anchor.dto.AnchorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

// HTTP POST 요청을 Node.js 서버로 보내는 클라이언트 역할.
@Component @RequiredArgsConstructor @Slf4j
public class AnchoringClient {
    private final WebClient nodeClient;

    @Value("${cleanus.anchor.node.retry.max-attempts:3}")
    private int maxAttempts;

    @Value("${cleanus.anchor.node.retry.backoff-ms:200")
    private int backoffMs;

    @Value("${cleanus.anchor.node.read-timeout-sec:10}")
    private int readTimeoutSec;

    public AnchorResponse anchor(AnchorRequest req) {
        log.info("Anchor request start: batchId={}, leaves={}", req.batchId(),
                (req.leaves() == null ? 0 : req.leaves().size()));

        return nodeClient.post()
                .uri("/api/anchor")
                .bodyValue(req)
                .retrieve()
                .onStatus(
                        s -> s.is4xxClientError() || s.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                .defaultIfEmpty("no body")
                                .map(msg -> {
                                    log.error("Node anchor HTTP error. status={}, body={}", resp.statusCode(), msg);
                                    return new RuntimeException("Node anchor failed: " + msg);
                                })
                )
                .bodyToMono(AnchorResponse.class)
                .timeout(Duration.ofSeconds(readTimeoutSec))
                .retryWhen(
                        Retry.backoff(Math.max(0, maxAttempts - 1), Duration.ofMillis(backoffMs))
                                .filter(ex -> !(ex instanceof IllegalArgumentException))
                )
                .doOnSuccess(res -> log.info("Anchor response ok : batchId={}, tx={}, block={}, merkleRoot={}",
                        res.batchId(), res.onchainTxHash(), res.blockNumber(), res.merkleRoot()))
                .doOnError(e-> log.error("Anchor request failed: batchId={}", req.batchId(), e))
                .block();
    }
}
