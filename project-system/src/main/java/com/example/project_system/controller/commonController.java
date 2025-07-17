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


    @PostMapping("/api/login1")
    public ResponseEntity login1(@RequestBody Map<String, String> params,
                                 HttpServletResponse res) {
        String java_userid = params.get("s_userid");
        String java_userpass = params.get("s_userpass");
        log.info(java_userid + ":" + java_userpass);

        String sql = "SELECT * FROM basemp WHERE userid = ? and userpass = ?";
        try {
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, java_userid);
            query.setParameter(2, java_userpass);
            List<Object[]> results = query.getResultList();
            if (!results.isEmpty()) {
                Object[] row = results.get(0);
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("userid", row[0]);
                resultMap.put("userpass", row[1]);
                resultMap.put("username", row[2]);
                resultMap.put("usermail", row[3]);
                return ResponseEntity.ok("OK");
            }
            else {
                return ResponseEntity.ok("NOT");
            }
        } catch (Exception e) {
            log.error("Login error: ", e);
            return ResponseEntity.internalServerError().body("Exception Login failed");
        }
    }

    @PostMapping("/api/login7")
    public ResponseEntity<?> login7(@RequestBody Map<String, String> params) {
        String server_username = params.get("s_username");
        log.info("넘어온 검색이름: {}", server_username);

        String sql = "SELECT * FROM basemp WHERE username like ?";
        try {
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, server_username + '%');
            List<Object[]> results = query.getResultList();
            log.info("results: {}", results);

            if (!results.isEmpty()) {
                List<Map<String, Object>> resultList = new ArrayList<>();

                for (Object[] row : results) {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("userid", row[0]);
                    resultMap.put("userpass", row[1]);
                    resultMap.put("username", row[2]);
                    resultMap.put("usermail", row[3]);
                    resultList.add(resultMap);
                }

                log.info("조회 결과: {}", resultList);
                return ResponseEntity.ok(resultList);
            }
            else {
                return ResponseEntity.ok("NOT"); 
            }
        } catch (Exception e) {
            log.error("Login error: ", e);
            return ResponseEntity.internalServerError().body("Exception Login failed");
        }
    }




    @GetMapping("/api/search1")
    public ResponseEntity<Object> search1(@RequestParam String username) {
        String sql = "SELECT * FROM basemp WHERE username Like ?";
        try {
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, '%' + username + '%');

            List<Object[]> results = query.getResultList();
            List<Map<String, Object>> jsonResults = new ArrayList<>();

            // 모든 결과를 Map으로 변환
            for (Object[] row : results) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("userid", row[0]);
                resultMap.put("userpass", row[1]);
                resultMap.put("username", row[2]);
                resultMap.put("usermail", row[3]);
                jsonResults.add(resultMap);
            }

            if (!results.isEmpty()) {
                log.info("조회 결과: " + jsonResults);
                return ResponseEntity.ok(jsonResults);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("status", "NOT");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            log.error("Login error: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Exception Login failed");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }


    @PostMapping("/api/insert1")
    public ResponseEntity insert1(@RequestBody Map<String, String> params,
                                  HttpServletResponse res) {

        String sql = "SELECT * FROM basemp WHERE userid = ?";
        try {
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, params.get("userid"));
            List<Object[]> results = query.getResultList();

            if (!results.isEmpty()) {
                return ResponseEntity.ok("Not Insert");
            }

            else {
                String sql1 = "INSERT INTO basemp(userid,userpass,username,usermail) VALUES (?,?,?,?)";
                Query query1 = entityManager.createNativeQuery(sql1);
                query1.setParameter(1, params.get("userid"));
                query1.setParameter(2, params.get("userpass"));
                query1.setParameter(3, params.get("username"));
                query1.setParameter(4, params.get("usermail"));
                int result = query1.executeUpdate();
                if(result > 0) {
                    return ResponseEntity.ok("Insert");
                }
                else {
                    return ResponseEntity.ok("Not Insert");
                }
            }
        } catch (Exception e) {
            log.error("Insert error: ", e);
            return ResponseEntity.internalServerError().body("Exception Insert failed");
        }
    }

    @PostMapping("/api/update1")
    public ResponseEntity update1(@RequestBody Map<String, String> params,
                                  HttpServletResponse res) {

        String sql = "SELECT * FROM basemp WHERE userid = ?";
        try {
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, params.get("userid"));
            List<Object[]> results = query.getResultList();

            if (results.isEmpty()) {
                return ResponseEntity.ok("No Update");
            }

            else {
                String sql1 = "UPDATE basemp SET userpass = ?, username =?, usermail=? WHERE userid = ?";
                Query query1 = entityManager.createNativeQuery(sql1);
                query1.setParameter(1, params.get("userpass"));
                query1.setParameter(2, params.get("username"));
                query1.setParameter(3, params.get("usermail"));
                query1.setParameter(4, params.get("userid"));

                int result = query1.executeUpdate();
                if(result > 0) {
                    return ResponseEntity.ok("Updated");
                }
                else {
                    return ResponseEntity.ok("No Update");
                }
            }
        } catch (Exception e) {
            log.error("Insert error: ", e);
            return ResponseEntity.internalServerError().body("Exception Update failed");
        }
    }

    @PostMapping("/api/delete1")
    public ResponseEntity delete1(@RequestBody Map<String, String> params,
                                  HttpServletResponse res) {

        String sql = "SELECT * FROM basemp WHERE userid = ?";
        try {
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, params.get("userid"));
            List<Object[]> results = query.getResultList();

            if (results.isEmpty()) {
                return ResponseEntity.ok("No Delete");
            }

            else {
                String sql1 = "DELETE FROM  basemp WHERE userid = ?";
                Query query1 = entityManager.createNativeQuery(sql1);
                query1.setParameter(1, params.get("userid"));
                int result = query1.executeUpdate();
                if(result > 0) {
                    return ResponseEntity.ok("Deleted");
                }
                else {
                    return ResponseEntity.ok("No Delete");
                }
            }
        } catch (Exception e) {
            log.error("Insert error: ", e);
            return ResponseEntity.internalServerError().body("Exception Delete failed");
        }
    }

    @PostMapping("/api/delete2")
    public ResponseEntity<String> delete2(@RequestBody Map<String, List<String>> params,
                                          HttpServletResponse res) {
        List<String> userids = params.get("userids");
        if (userids == null || userids.isEmpty()) {
            return ResponseEntity.ok("No Delete");
        }
        try {
            log.info("userids: {}", userids);

            // 개별 사용자 삭제 체크 및 로그 추가
            String sqlCheck = "SELECT * FROM basemp WHERE userid = ?";
            String sqlDelete = "DELETE FROM basemp WHERE userid = ?";

            for (String userid : userids) {
                // 개별 사용자 존재 여부 확인
                Query queryCheck = entityManager.createNativeQuery(sqlCheck);
                queryCheck.setParameter(1, userid);
                List<Object[]> results = queryCheck.getResultList();

                if (results.isEmpty()) {
                    log.info("삭제 대상이 아님 (존재하지 않음): {}", userid);
                    continue;  // 해당 userid는 삭제하지 않음
                }

                // 개별 사용자 삭제
                Query queryDelete = entityManager.createNativeQuery(sqlDelete);
                queryDelete.setParameter(1, userid);
                int result = queryDelete.executeUpdate();

                if (result > 0) {
                    log.info("삭제 성공: {}", userid);
                } else {
                    log.info("삭제 실패: {}", userid);
                }
            }

            return ResponseEntity.ok("Deleted");

        } catch (Exception e) {
            log.error("Delete error: ", e);
            return ResponseEntity.internalServerError().body("Exception Delete failed");
        }
    }





    @Autowired
    private UserImageRepository userImageRepository;
    // 이미지 저장 경로 설정 (루트경로 + /upload/images/)
    private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "upload", "images");

    @PostMapping("/api/upload1")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("userid") String userId,
            @RequestParam("image") MultipartFile image) {

        if (image.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "이미지가 없습니다."));
        }

        try {
            // 저장 경로 폴더가 없으면 생성
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 고유 파일명 생성 (UUID + 원래 파일명)
            String originalFilename = image.getOriginalFilename();
            String newFilename = UUID.randomUUID() + "_" + originalFilename;

            // 파일 저장
            Path filePath = uploadDir.resolve(newFilename);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // DB에 저장
            String sql = "insert into basimg (userid, userimg) values (?, ?)";
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, userId);
            query.setParameter(2, filePath.toString());
            query.executeUpdate();

            // 프론트에서 접근 가능한 이미지 URL
            String imageUrl = "/images/" + newFilename;
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "파일 저장 실패"));
        }
    }

    @GetMapping("/api/getImages")
    public ResponseEntity<List<String>> getImageList() {
        try {
            // DB에서 이미지 경로 조회
            String sql = "SELECT userimg FROM basimg";
            List<String> imagePaths = entityManager.createNativeQuery(sql).getResultList();

            // 각 경로에서 파일명만 추출해서 리스트에 담음
            List<String> imageFilenames = imagePaths.stream()
                    .map(path -> {
                        Path p = Paths.get(path.toString());
                        return p.getFileName().toString();
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(imageFilenames);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}