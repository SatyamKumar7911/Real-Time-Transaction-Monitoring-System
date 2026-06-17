package com.example.accountservice.controller;

import com.example.accountservice.entity.LogEntity;
import com.example.accountservice.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Slf4j
public class LogController {
    
    private final LogService logService;
    
    /**
     * Get all logs with pagination
     * GET /api/logs?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<Page<LogEntity>> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<LogEntity> logs = logService.getAllLogs(pageable);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Get logs by account number
     * GET /api/logs/account/AC1234567890
     */
    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<LogEntity>> getLogsByAccount(@PathVariable String accountNumber) {
        List<LogEntity> logs = logService.getLogsByAccountNumber(accountNumber);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Get logs by transaction ID
     * GET /api/logs/transaction/TXN123456
     */
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<LogEntity>> getLogsByTransactionId(@PathVariable String transactionId) {
        List<LogEntity> logs = logService.getLogsByTransactionId(transactionId);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Get logs by transaction status
     * GET /api/logs/status/COMPLETED
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LogEntity>> getLogsByStatus(@PathVariable String status) {
        List<LogEntity> logs = logService.getLogsByTransactionStatus(status);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Get logs by event source (KAFKA or DIRECT)
     * GET /api/logs/source/KAFKA
     */
    @GetMapping("/source/{eventSource}")
    public ResponseEntity<List<LogEntity>> getLogsByEventSource(@PathVariable String eventSource) {
        List<LogEntity> logs = logService.getLogsByEventSource(eventSource);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Get recent Kafka transaction logs
     * GET /api/logs/kafka/recent
     */
    @GetMapping("/kafka/recent")
    public ResponseEntity<List<LogEntity>> getRecentKafkaLogs() {
        List<LogEntity> logs = logService.getRecentKafkaLogs(10);
        return ResponseEntity.ok(logs);
    }
}