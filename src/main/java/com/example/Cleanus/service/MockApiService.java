package com.example.Cleanus.service;

import com.example.Cleanus.entity.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Service
public class MockApiService {

    private static final String MOCK_API_URL = "http://localhost:3000/transactions";

    public List<Transaction> fetchTransactions(String fromDate, String toDate, String inoutType) {
        RestTemplate restTemplate = new RestTemplate();

        String url = UriComponentsBuilder.fromHttpUrl(MOCK_API_URL)
                .queryParam("from_date", fromDate)
                .queryParam("to_date", toDate)
                .queryParam("inout_type", inoutType)
                .toUriString();

        Transaction[] transactions = restTemplate.getForObject(url, Transaction[].class);
        return Arrays.asList(transactions);
    }
}