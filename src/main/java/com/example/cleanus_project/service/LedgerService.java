package com.example.cleanus_project.service;

import com.example.cleanus_project.dto.BlockchainRecordRequest;
import com.example.cleanus_project.entity.Transaction;
import com.example.cleanus_project.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.List;

@Service
public class LedgerService {

    @Autowired
    private MockApiService mockApiService;

    @Autowired
    private TransactionRepository transactionRepository;

    private final String NODE_SERVER_URL = "http://localhost:4000/blockchain/record";

    public void fetchAndSave(String fromDate, String toDate, String inoutType) {
        List<Transaction> transactions = mockApiService.fetchTransactions(fromDate,toDate,inoutType);

        for (Transaction tx : transactions) {
            boolean exists = transactionRepository.existsByDateAndShopNameAndWithdrawAmount(
                    tx.getDate(), tx.getShopName(), tx.getAmount()
            );

            if (!exists) {
                transactionRepository.save(tx);
                sendToNodeServer(tx);
            }
        }
    }

    private void sendToNodeServer(Transaction tx) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        BlockchainRecordRequest record = new BlockchainRecordRequest(
                tx.getDate(), tx.getShopName(), tx.getAmount()
        );

        HttpEntity<BlockchainRecordRequest> request = new HttpEntity<>(record, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(NODE_SERVER_URL, request, String.class);
            System.out.println("블록체인 기록 결과: " + response.getBody());
        } catch (Exception e) {
            System.err.println("블록체인 전송 실패: " + e.getMessage());
        }
    }
}