package com.example.cleanus_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class MainController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/main")
    public ResponseEntity<List<Map<String, Object>>> getProcessedData(
            @RequestParam String fromDate,
            @RequestParam String toDate,
            @RequestParam(defaultValue = "A") String inout_type
    ) {
        try {
            // 1. API 서버(8001 포트)의 mock1 호출
            String apiUrl = String.format(
                    "http://localhost:8001/api/mock1?fromDate=%s&toDate=%s&inout_type=%s",
                    fromDate, toDate, inout_type
            );

            log.info("API 서버 호출: {}", apiUrl);

            // 2. API 서버에서 데이터 받기
            ResponseEntity<List<Map<String, Object>>> apiResponse = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            List<Map<String, Object>> apiData = apiResponse.getBody();
            log.info("API 서버에서 받은 데이터 건수: {}", apiData != null ? apiData.size() : 0);

            // 3. 프론트엔드가 기대하는 형태 그대로 반환
            return ResponseEntity.ok(apiData);

        } catch (Exception e) {
            log.error("API 호출 실패", e);
            // 빈 리스트 반환 (프론트엔드에서 "조회된 거래가 없습니다" 메시지 표시)
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/")
    public String home() {
        return "메인 서버 홈페이지 (포트 8000)";
    }
}