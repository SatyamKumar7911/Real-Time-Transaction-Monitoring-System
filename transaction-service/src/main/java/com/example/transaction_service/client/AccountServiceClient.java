package com.example.transaction_service.client;

import com.example.transaction_service.dto.BalanceResponse;
import com.example.transaction_service.dto.CreditDebitRequest;
import com.example.transaction_service.dto.TransferRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Feign client for communicating with Account Service
 */
@FeignClient(name = "account-service", url = "${app.account-service.url:http://localhost:8081}")
public interface AccountServiceClient {
    
    @GetMapping("/api/v1/accounts/{accountNumber}/balance")
    BalanceResponse getBalance(@PathVariable("accountNumber") String accountNumber);
    
    @PostMapping("/api/v1/accounts/credit")
    BalanceResponse credit(@RequestBody CreditDebitRequest request);
    
    @PostMapping("/api/v1/accounts/debit")
    BalanceResponse debit(@RequestBody CreditDebitRequest request);
    
    @PostMapping("/api/v1/accounts/transfer")
    BalanceResponse transfer(@RequestBody TransferRequest request);
    
    @GetMapping("/api/v1/accounts/{accountNumber}")
    BalanceResponse getAccountDetails(@PathVariable("accountNumber") String accountNumber);
}