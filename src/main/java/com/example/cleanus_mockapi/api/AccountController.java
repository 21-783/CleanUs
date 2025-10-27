package com.example.cleanus_mockapi.api;

import com.example.cleanus_mockapi.account.Account;
import com.example.cleanus_mockapi.account.AccountRepository;
import com.example.cleanus_mockapi.api.dto.RegisterAccountReq;
import com.example.cleanus_mockapi.api.dto.RegisterAccountRes;
import com.example.cleanus_mockapi.common.HashUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepo;

    @PostMapping(value = "/accounts", consumes = MediaType.APPLICATION_JSON_VALUE)
    public RegisterAccountRes register(@RequestBody @Valid RegisterAccountReq req) {
        String fintechUseNum = System.currentTimeMillis()
                + "-" + req.bankCode()
                + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        String last4 = req.accountNumber().substring(Math.max(0, req.accountNumber().length() - 4));

        Account saved = accountRepo.save(Account.builder()
                .fintechUseNum(fintechUseNum)
                .bankCode(req.bankCode())
                .accountNumberHash(HashUtils.sha256Hex(req.accountNumber()))
                .accountLast4(last4)
                .initialBalance( randomRange(500_000L, 5_500_000L) )
                .status(Account.Status.ACTIVE)
                .registeredAt(Instant.now())
                .build());

        return new RegisterAccountRes(
                saved.getFintechUseNum(),
                saved.getBankCode(),
                saved.getAccountLast4(),
                saved.getStatus().name(),
                saved.getRegisteredAt().toString()
        );
    }

    private static long randomRange(long min, long max) {
        return min + (long) (Math.random() * (max - min + 1));
    }
}
