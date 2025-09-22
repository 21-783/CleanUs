package com.example.cleanus_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
            @RequestParam(defaultValue = "A") String inout_type,
            @RequestParam Integer groupNum,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            // 1. API 서버(8001 포트)의 transactions 호출
            String apiUrl = String.format(
                    "http://localhost:8001/api/transactions?fromDate=%s&toDate=%s&inout_type=%s&groupNum=%s&page=%s&size=%s",
                    fromDate, toDate, inout_type, groupNum, page, size
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

    //그룹 추가 요청 → Mock 서버 호출
    @PostMapping("/addGroup")
    public ResponseEntity<Map<String, Object>> addGroup(
            @RequestParam(defaultValue = "1") int step,
            @RequestParam String bank_name,
            @RequestParam String account_num
    ) {
        try {
            String apiUrl = String.format(
                    "http://localhost:8001/api/increaseGroupMax?step=%d&bankName=%s&accountNum=%s",
                    step, bank_name, account_num
            );

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("error"))
                return ResponseEntity.status(400).body(body); //중복 계좌 에러 프론트 전달

            log.info("Mock 서버 그룹 증가 호출 성공: {}", response.getBody());
            return response;
        } catch (Exception e) {
            log.error("Mock 서버 그룹 증가 호출 실패", e);
            return ResponseEntity.status(500).body(Map.of("error", "그룹 추가 실패"));
        }
    }

    @GetMapping("/")
    public String home() {
        return "메인 서버 홈페이지 (포트 8000)";
    }
}