package com.example.Cleanus.account;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ledgers/{ledgerId}/account")
public class AccoutController {
    private final AccountService accountService;

    private Long requireLogin(HttpSession session) {
        Long userId = (Long) session.getAttribute("loginUser");
        if (userId == null) throw new RuntimeException("로그인이 필요합니다.");
        return userId;
    }

    @PostMapping
    public ResponseEntity<?> create(@PathVariable Long ledgerId,
                                    @Valid @RequestBody UpsertAccountRequest req,
                                    HttpSession session) {
        requireLogin(session);
        Account acc = accountService.createForLedger(ledgerId, req.bankName, req.accountNumber, req.displayName);
        return ResponseEntity.ok(new AccountResponse(acc.getId(), acc.getDisplayName(), acc.getBankName(), acc.getAccountLast4()));
    }

    @PutMapping
    public ResponseEntity<?> replace(@PathVariable Long ledgerId,
                                     @Valid @RequestBody UpsertAccountRequest req,
                                     HttpSession session) {
        requireLogin(session);
        Account acc = accountService.replaceForLedger(ledgerId, req.bankName, req.accountNumber, req.displayName);
        return ResponseEntity.ok(new AccountResponse(acc.getId(), acc.getDisplayName(), acc.getBankName(), acc.getAccountLast4()));
    }

    @GetMapping
    public ResponseEntity<?> get(@PathVariable Long ledgerId, HttpSession session) {
        requireLogin(session);
        Account acc = accountService.getByLedger(ledgerId);
        return ResponseEntity.ok(new AccountResponse(acc.getId(), acc.getDisplayName(), acc.getBankName(), acc.getAccountLast4()));
    }

    //DTO
    @Data
    public static class UpsertAccountRequest {
        @NotBlank
        private String bankName;
        @NotBlank private String accountNumber; // 평문 입력 -> 서버에서 해시
        private String displayName;
    }

    @Data
    public static class AccountResponse {
        private final Long id;
        private final String displayName;
        private final String bankName;
        private final String last4;
    }
}
