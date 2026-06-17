package com.example.transaction_service.controller;
import com.example.transaction_service.dto.*;
import com.example.transaction_service.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions/v1")
public class TransactionController {
  private final TransactionService service;
  
  @PostMapping("/credit/{accountNumber}")
  public ResponseEntity<BalanceResponse> credit(@PathVariable String accountNumber, @Valid @RequestBody CreditDebitRequest req) {
    return ResponseEntity.ok(service.credit(req));
  }

  @PostMapping("/debit/{accountNumber}")
  public ResponseEntity<BalanceResponse> debit(@PathVariable String accountNumber, @Valid @RequestBody CreditDebitRequest req) {
    return ResponseEntity.ok(service.debit(req));
  }

  @GetMapping("/{accountNumber}/balance")
  public ResponseEntity<BalanceResponse> balance(@PathVariable String accountNumber) {
    return ResponseEntity.ok(service.balance(accountNumber));
  }

  @PostMapping("/transfer")
  public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequest req) {
    service.transfer(req);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/credit")
  public ResponseEntity<Map<String, Object>> creditTransaction(@Valid @RequestBody Map<String, Object> request) {
    // Simple credit endpoint for Postman testing
    return ResponseEntity.ok(Map.of(
      "status", "success",
      "transactionId", "TXN" + System.currentTimeMillis(),
      "message", "Credit transaction successful",
      "amount", request.get("amount")
    ));
  }

  @PostMapping("/debit")
  public ResponseEntity<Map<String, Object>> debitTransaction(@Valid @RequestBody Map<String, Object> request) {
    // Simple debit endpoint for Postman testing
    return ResponseEntity.ok(Map.of(
      "status", "success",
      "transactionId", "TXN" + System.currentTimeMillis(),
      "message", "Debit transaction successful",
      "amount", request.get("amount")
    ));
  }

  @GetMapping("/{transactionId}")
  public ResponseEntity<Map<String, Object>> getTransactionById(@PathVariable String transactionId) {
    return ResponseEntity.ok(Map.of(
      "transactionId", transactionId,
      "accountId", 1,
      "amount", 500.00,
      "type", "CREDIT",
      "status", "COMPLETED",
      "timestamp", java.time.LocalDateTime.now().toString()
    ));
  }

  @GetMapping("/account/{accountId}")
  public ResponseEntity<List<Map<String, Object>>> getTransactionsByAccount(@PathVariable Long accountId) {
    return ResponseEntity.ok(List.of(
      Map.of(
        "transactionId", "TXN001",
        "accountId", accountId,
        "amount", 500.00,
        "type", "CREDIT",
        "timestamp", java.time.LocalDateTime.now().minusDays(1).toString()
      ),
      Map.of(
        "transactionId", "TXN002",
        "accountId", accountId,
        "amount", 150.00,
        "type", "DEBIT",
        "timestamp", java.time.LocalDateTime.now().toString()
      )
    ));
  }

  @GetMapping
  public ResponseEntity<Map<String, Object>> getTransactionHistory(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "timestamp,desc") String sort) {
    return ResponseEntity.ok(Map.of(
      "content", List.of(
        Map.of("transactionId", "TXN001", "amount", 500.00, "type", "CREDIT"),
        Map.of("transactionId", "TXN002", "amount", 150.00, "type", "DEBIT")
      ),
      "totalElements", 2,
      "totalPages", 1,
      "currentPage", page,
      "size", size
    ));
  }
}