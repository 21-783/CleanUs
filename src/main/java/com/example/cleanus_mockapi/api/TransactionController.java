package com.example.cleanus_mockapi.api;
import com.example.cleanus_mockapi.account.AccountRepository;
import com.example.cleanus_mockapi.api.dto.TransactionItem;
import com.example.cleanus_mockapi.api.dto.TransactionListRes;
import com.example.cleanus_mockapi.transaction.Transaction;
import com.example.cleanus_mockapi.transaction.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionRepository txRepo;
    private final AccountRepository accountRepo;

    @GetMapping("/transactions")
    public ResponseEntity<?> list(@RequestParam String fintechUseNum,
                                  @RequestParam String from,
                                  @RequestParam String to) {
        if (!accountRepo.existsById(fintechUseNum)) {
            return ResponseEntity.status(404).body(
                    java.util.Map.of("code","NOT_FOUND","message","Account not found","fintechUseNum",fintechUseNum)
            );
        }

        Instant f = Instant.parse(from);
        Instant t = Instant.parse(to);
        if (!t.isAfter(f)) {
            return ResponseEntity.badRequest().body(
                    java.util.Map.of("code","BAD_REQUEST","message","to must be after from")
            );
        }

        List<Transaction> list = txRepo.findByFintechUseNumAndTxAtBetweenOrderByTxAtAsc(fintechUseNum, f, t);
        List<TransactionItem> items = list.stream()
                .map(x -> new TransactionItem(
                        x.getProviderTxId(),
                        x.getTxAt().toString(),
                        x.getShopName(),
                        x.getAmount().toPlainString()
                ))
                .toList();

        return ResponseEntity.ok(new TransactionListRes(fintechUseNum, items));
    }
}