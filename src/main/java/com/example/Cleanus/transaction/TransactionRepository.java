package com.example.Cleanus.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    // Mock API로 부터 받아온 이 거래(providerTxId)가 이미 Ledger안에 저장되어 있는지 확인. (멱등성 체크)
    boolean existsByLedgerIdAndProviderTxId(Long ledgerId, String providerTxId);

    // Batch 봉인(Seal)에 사용. (특정 기간 동안 발생한 거래를 모은다.)
    List<Transaction> findByLedgerIdAndTxAtBetween(Long ledgerId, Instant from, Instant to);

    // Batch 봉인(Seal)에 사용. (이미 봉인된 배치 이후의 거래들을 모은다.)
    // Proof 계산에 사용.
    List<Transaction> findByLedgerIdAndTxAtAfter(Long ledgerId, Instant after);
}
