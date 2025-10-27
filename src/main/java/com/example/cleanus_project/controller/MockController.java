package com.example.cleanus_project.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@Slf4j
@Transactional
@RequestMapping("/api")
public class MockController {
    @PersistenceContext
    private EntityManager entityManager;

    private static final List<String> DESCRIPTIONS = List.of(
            "전기요금", "아이스돔", "다이소"
    ); //상점명
    private static final Random RANDOM = new Random();
    private Map<Integer, Integer> groupBalances = new HashMap<>(); //그룹별 잔액 관리 맵

    //가짜 거래내역 생성
    private Map<String, Object> generateMockTransaction(Integer groupNum, String bankName, String accountNum) {

        LocalDateTime now = LocalDateTime.now();

        String description = DESCRIPTIONS.get(RANDOM.nextInt(DESCRIPTIONS.size()));

        //그룹 초기 잔액 설정
        groupBalances.putIfAbsent(groupNum, 1_000_000);
        int balance = groupBalances.get(groupNum);

        //거래 금액 랜덤 생성
        int rawAmount = RANDOM.nextInt(900_000) + 100_000;
        boolean isWithdraw = RANDOM.nextBoolean();
        int amount = isWithdraw ? -rawAmount : rawAmount;

        //잔액 음수 방지
        if (balance + amount < 0) amount = rawAmount;

        balance += amount;
        groupBalances.put(groupNum, balance);

        //거래 내역 Map 생성
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("groupNum", groupNum);
        result.put("bankName", bankName);
        result.put("accountNum", accountNum);
        result.put("tranDateTime", now);
        result.put("description", description);
        result.put("amount", amount);
        result.put("balance", balance);
        result.put("tranDateObj", now); // 내부 필터용

        return result;
    }

  //DB에 거래내역 저장
    @Scheduled(fixedRate = 600 * 1000)
    public void generateAndStoreTransaction() {
        try {
            //DB에서 그룹 정보 조회
            Query groupQuery = entityManager.createNativeQuery("SELECT groupNum, bankName, accountNum FROM groups ORDER BY groupNum ASC");
            List<Object[]> groups = groupQuery.getResultList();

            for (Object[] g : groups) {
                Integer groupNum = (Integer) g[0];
                String bankName = (String) g[1];
                String accountNum = (String) g[2];

                Map<String, Object> tx = generateMockTransaction(groupNum, bankName, accountNum);

                //mock_transactions 테이블에 저장
                String sql = "INSERT INTO mock_transactions (groupNum, date, description, amount, balance) " +
                        "VALUES (?, ?, ?, ?, ?)";
                Query insertQuery = entityManager.createNativeQuery(sql);
                insertQuery.setParameter(1, tx.get("groupNum"));
                insertQuery.setParameter(2, tx.get("tranDateTime"));
                insertQuery.setParameter(3, tx.get("description"));
                insertQuery.setParameter(4, tx.get("amount"));
                insertQuery.setParameter(5, tx.get("balance"));

                int result = insertQuery.executeUpdate();
                if (result > 0) log.info("거래내역 저장 완료");
                else log.warn("거래내역 저장 실패: {}", tx);
            }

        } catch (Exception e) {
            log.error("자동 거래 생성 중 오류 발생", e);
        }
    }

    //거래내역 조회 <-금융결제원 잔액조회 api 모방
    @GetMapping("/transactions")
    public List<Map<String, Object>> getFilteredTransactions(
            @RequestParam String fromDate,
            @RequestParam String toDate,
            @RequestParam Integer groupNum,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        LocalDate from = LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate to = LocalDate.parse(toDate, DateTimeFormatter.ofPattern("yyyyMMdd"));

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.atTime(23, 59, 59);

        int offset = (page - 1) * size;

        String sql = "SELECT t.id, t.groupNum, g.userName, g.bankName, g.accountNum, t.date, t.description, t.amount, t.balance " +
                "FROM mock_transactions t " +
                "JOIN groups g ON t.groupNum = g.groupNum " +
                "WHERE t.date BETWEEN ? AND ?" +
                (groupNum != null ? " AND t.groupNum = ? " : "") +
                "ORDER BY t.date DESC " +
                "LIMIT ? OFFSET ?";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter(1, fromDateTime);
        query.setParameter(2, toDateTime);

        int paramIndex = 3;
        if (groupNum != null) {
            query.setParameter(paramIndex++, groupNum);
        }
        query.setParameter(paramIndex++, size);
        query.setParameter(paramIndex, offset);

        List<Object[]> rows = query.getResultList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", row[0]);
            map.put("groupNum", row[1]);
            map.put("userName", row[2]);
            map.put("bankName", row[3]);
            map.put("accountNum", row[4]);
            map.put("date", row[5]);
            map.put("description", row[6]);
            map.put("amount", row[7]);
            map.put("balance", row[8]);

            result.add(map);
        }

        return result;
    }

    //메인서버에서 받은 그룹 추가
    @PostMapping("/increaseGroupMax")
    public Map<String, Object> increaseGroupMax(
            @RequestParam String userName,
            @RequestParam String bankName,
            @RequestParam String accountNum
    ) {
        //그룹 중복 계좌 확인
        Query checkQuery = entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM groups WHERE userName = ? AND bankName = ? AND accountNum = ?"
        );
        checkQuery.setParameter(1, userName);
        checkQuery.setParameter(2, bankName);
        checkQuery.setParameter(3, accountNum);

        Number count = (Number) checkQuery.getSingleResult();
        if (count.intValue() > 0) {
            log.warn("이미 존재하는 계좌-은행으로 그룹 생성 시도: {}-{}-{}", userName, bankName, accountNum);
            return Map.of(
                    "error", "이미 존재하는 계좌-은행입니다"
            );
        }

        //새 그룹 번호 결정 (max(groupNum) + 1)
        Query maxQuery = entityManager.createNativeQuery("SELECT COALESCE(MAX(groupNum), 0) FROM groups");
        Integer maxGroupNum = ((Number) maxQuery.getSingleResult()).intValue();
        int newGroupNum = maxGroupNum + 1;

        //groups에 신규 그룹 추가
        Query insertQuery = entityManager.createNativeQuery(
                "INSERT INTO groups (groupNum, userName, bankName, accountNum) VALUES (?, ?, ?, ?)"
        );
        insertQuery.setParameter(1, newGroupNum);
        insertQuery.setParameter(2, userName);
        insertQuery.setParameter(3, bankName);
        insertQuery.setParameter(4, accountNum);
        insertQuery.executeUpdate();

        log.info("새 그룹 생성 완료: groupNum={}, userName={}, bankName={}, accountNum={}",
                newGroupNum, userName, bankName, accountNum);

        return Map.of(
                "newGroup", newGroupNum,
                "userName", userName
        );
    }

}
