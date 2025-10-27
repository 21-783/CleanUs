package com.example.Cleanus.ingest;

import com.example.Cleanus.hash.LeafHashUtil;
import com.example.Cleanus.provider.MockApiTxnClient;
import com.example.Cleanus.provider.dto.TransactionListRes;
import com.example.Cleanus.transaction.Transaction;
import com.example.Cleanus.transaction.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

// MockApi로부터 거래 데이터를 받아와서 해시처리 하여 내부 DB에 저장하는 역할.
@Service @RequiredArgsConstructor
public class TransactionIngestService {
    private final MockApiTxnClient mockApi;
    private final TransactionRepository txRepo;

    @Transactional
    public int ingestRange(Long ledgerId, String fintechUseNum, Instant from, Instant to) {
        TransactionListRes res = mockApi.fetchTransactions(fintechUseNum, from.toString(), to.toString());
        if (res == null || res.items() == null) return 0;

        int saved = 0;
        for (var it : res.items()) {
            if(txRepo.existsByLedgerIdAndProviderTxId(ledgerId, it.providerTxId())) continue;

            Instant txAt = Instant.parse(it.txAt());
            var amt = new BigDecimal(it.amount());
            var leafJson = LeafHashUtil.canonicalLeafJson(res.fintechUseNum(), txAt, it.shopName(), amt, it.providerTxId());
            var leaf = LeafHashUtil.keccak256Hex(leafJson);

            var tx = Transaction.builder()
                    .ledgerId(ledgerId).fintechUseNum(res.fintechUseNum())
                    .providerTxId(it.providerTxId()).txAt(txAt)
                    .shopName(it.shopName()).amount(amt).leafHash(leaf)
                    .build();
            txRepo.save(tx);
            saved++;
         }
        return saved;
    }
}
