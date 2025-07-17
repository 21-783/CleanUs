package com.example.project_system.controller;

import com.example.project_system.repository.UserImageRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
@Transactional

public class commonController {
    public commonController() {
        log.info("common Controller 생성자");
    }
    @PersistenceContext // 어노테이션 EntityManager을 사용하기 쉽도록
    private EntityManager entityManager; // EntityManager 주입


    //캡디 api 시작
    private static final List<Map<String, String>> BANK_ACCOUNTS = List.of(
            Map.of("bank_name", "KYONGNAM BANK", "account_num", "221-0000-1234-56"),
            Map.of("bank_name", "KAKAOBANK", "account_num", "3333-12-3456789"),
            Map.of("bank_name", "WOORI BANK", "account_num", "1002-987-654321")
    );

    private static final List<String> DESCRIPTIONS = List.of(
            "전기요금", "아이스돔", "지에스25부경대학교", "주식회사위드컬쳐", "부경대학교소비자생활","다이소"
    );

    private static final Random RANDOM = new Random();

    private final List<Map<String, Object>> transactionHistory = new ArrayList<>();

    // 1. JSON 생성 함수
    private Map<String, Object> generateMockTransaction() {
        Map<String, String> bankAccount = BANK_ACCOUNTS.get(RANDOM.nextInt(BANK_ACCOUNTS.size()));
        LocalDateTime now = LocalDateTime.now();
        String tranDateTime = now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
        String description = DESCRIPTIONS.get(RANDOM.nextInt(DESCRIPTIONS.size()));
        boolean isWithdraw = RANDOM.nextBoolean();
        int amount = RANDOM.nextInt(100_000) + 1;

        int withdrawnAmount = isWithdraw ? amount : 0;
        int depositedAmount = isWithdraw ? 0 : amount;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("bank_name", bankAccount.get("bank_name"));
        result.put("account_num", bankAccount.get("account_num"));
        result.put("tran_date/time", tranDateTime);
        result.put("description", description);
        result.put("withdrawn_amount", withdrawnAmount);
        result.put("deposited_amount", depositedAmount);
        result.put("tran_date_obj", now); // 내부 필터링용 LocalDateTime 객체 저장

        return result;
    }

    // 2. 주기적으로 실행되는 스케줄링 함수
    //@Scheduled(fixedRate = 60 * 60 * 1000) // 1시간마다 실행
    @Scheduled(fixedRate = 60 * 1000) //1분마다 실행 (60 * 1000ms), 1000 곱하는 이유? 매개변수 단위가 ms 이기 때문이다.
    public void generateAndStoreTransaction() {
        Map<String, Object> newTransaction = generateMockTransaction();
        transactionHistory.add(newTransaction);

        //데이터 생성 확인용 println
        //System.out.println("Scheduled Transaction Added: " + newTransaction);
    }

    @GetMapping("/api/mock1")
    public List<Map<String, Object>> getFilteredTransactions(
            @RequestParam String fromDate,
            @RequestParam String toDate,
            @RequestParam(defaultValue = "A") String inout_type
    ) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        LocalDate from = LocalDate.parse(fromDate, dateFormatter);
        LocalDate to = LocalDate.parse(toDate, dateFormatter);

        List<Map<String, Object>> filtered = transactionHistory.stream()
                .filter(tx -> {
                    LocalDateTime tranDateTime = (LocalDateTime) tx.get("tran_date_obj");
                    LocalDate tranDate = tranDateTime.toLocalDate();
                    return !tranDate.isBefore(from) && !tranDate.isAfter(to);
                })
                .filter(tx -> {
                    if ("I".equalsIgnoreCase(inout_type)) {
                        return ((int) tx.get("deposited_amount")) > 0;
                    } else if ("O".equalsIgnoreCase(inout_type)) {
                        return ((int) tx.get("withdrawn_amount")) > 0;
                    }
                    return true;
                })
                .map(tx -> {
                    Map<String, Object> clone = new LinkedHashMap<>(tx);
                    clone.remove("tran_date_obj"); // 내부용 필드는 제거
                    return clone;
                })
                .collect(Collectors.toList());

        //반환되는 데이터 확인용 log.info
        //log.info("Filtered transactions sent to client: {}", filtered);

        return filtered;
    }

    //캡디 api 끝

}
