package com.example.Cleanus.account;

import com.example.Cleanus.ledger.Ledger;
import com.example.Cleanus.ledger.LedgerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final LedgerRepository ledgerRepository;
    private final AccountHasher hasher;

    @Transactional
    public Account createForLedger(Long ledgerId, String bankName, String rawAccountNumber, String displayNameOpt){
        Ledger ledger = ledgerRepository.findById(ledgerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장부는 존재하지 않습니다."));

        if(accountRepository.existsByLedger_Id(ledgerId)){
            throw new IllegalArgumentException("이미 이 장부에는 등록된 계좌가 존재합니다.");
        }

        String last4 = hasher.last4(rawAccountNumber);
        String hash = hasher.hashAccountNumber(rawAccountNumber);
        String display = (displayNameOpt != null && !displayNameOpt.isBlank())
                ? displayNameOpt
                : bankName + "**" + last4;

        Account acc = Account.builder()
                .ledger(ledger)
                .bankName(bankName)
                .accountNumberHash(hash)
                .accountLast4(last4)
                .displayName(display)
                .build();

        return accountRepository.save(acc);
    }

    @Transactional
    public Account replaceForLedger(Long ledgerId, String bankName, String rawAccountNumber, String displayNameOpt){
        ledgerRepository.findById(ledgerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장부는 존재하지 않습니다."));

        accountRepository.findByLedger_Id(ledgerId).ifPresent(accountRepository::delete);
        return createForLedger(ledgerId, bankName, rawAccountNumber, displayNameOpt);
    }

    public Account getByLedger(Long ledgerId) {
        return accountRepository.findByLedger_Id(ledgerId)
                .orElseThrow(() -> new IllegalArgumentException("이 장부에는 해당 계좌가 없습니다."));
    }


}
