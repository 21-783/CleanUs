package com.example.cleanus_mockapi.scheduler;
import com.example.cleanus_mockapi.account.AccountRepository;
import com.example.cleanus_mockapi.transaction.TransactionRepository;
import com.example.cleanus_mockapi.account.Account;
import com.example.cleanus_mockapi.transaction.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionGenerator {

    private final AccountRepository accountRepo;
    private final TransactionRepository txRepo;

    @Value("${mockapi.tx.min-per-minute}") int minPerMinute;
    @Value("${mockapi.tx.max-per-minute}") int maxPerMinute;
    @Value("${mockapi.tx.min-amount}") long minAmount;
    @Value("${mockapi.tx.max-amount}") long maxAmount;
    @Value("#{'${mockapi.tx.shops}'.split(',')}")
    List<String> shops;

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneOffset.UTC);

    @Scheduled(cron = "0 * * * * *") // 매 분 0초
    @Transactional
    public void generate() {
        List<Account> actives = accountRepo.findByStatus(Account.Status.ACTIVE);
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        for (Account a : actives) {
            int count = rnd.nextInt(minPerMinute, maxPerMinute + 1);

            BigDecimal lastBalance = txRepo.findTopByFintechUseNumOrderByTxAtDesc(a.getFintechUseNum())
                    .map(Transaction::getBalanceAfter)
                    .orElse(BigDecimal.valueOf(a.getInitialBalance()));

            for (int i = 0; i < count; i++) {
                // 금액 생성 (부호 포함)
                long raw = rnd.nextLong(Math.abs(minAmount), Math.abs(maxAmount) + 1);
                BigDecimal amount = BigDecimal.valueOf(raw);
                if (rnd.nextBoolean()) amount = amount.negate();

                // 거래 시각 (최근 50초에 분포)
                Instant txAt = Instant.now().minusSeconds(rnd.nextLong(0, 50));

                // 신규 잔액
                BigDecimal balanceAfter = lastBalance.add(amount);

                String providerTxId = "PTX-" + TS.format(txAt) + "-" + String.format("%06d", rnd.nextInt(1, 1_000_000));

                Transaction tx = Transaction.builder()
                        .providerTxId(providerTxId)
                        .fintechUseNum(a.getFintechUseNum())
                        .txAt(txAt)
                        .shopName(shops.get(rnd.nextInt(shops.size())))
                        .amount(amount)
                        .balanceAfter(balanceAfter)
                        .build();

                txRepo.save(tx);
                lastBalance = balanceAfter;
            }
        }
        if (!actives.isEmpty()) {
            log.info("TransactionGenerator: processed {} active accounts", actives.size());
        }
    }
}