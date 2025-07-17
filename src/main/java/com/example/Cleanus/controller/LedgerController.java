package com.example.Cleanus.controller;

import com.example.Cleanus.service.LedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ledger")
public class LedgerController {

    @Autowired
    private LedgerService ledgerService;

    @GetMapping("/fetch")
    public String fetchAndSave(@RequestParam String fromDate,
                               @RequestParam String toDate,
                               @RequestParam String inoutType) {
        ledgerService.fetchAndSave(fromDate, toDate, inoutType);
        return "Mock API 데이터 가져와서 저장 완료!";
    }
}