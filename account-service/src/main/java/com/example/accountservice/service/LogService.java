package com.example.accountservice.service;

import com.example.accountservice.entity.LogEntity;
import com.example.accountservice.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {
    
    private final LogRepository logRepository;
    
    /**
     * Get all logs with pagination
     */
    public Page<LogEntity> getAllLogs(Pageable pageable) {
        return logRepository.findAll(pageable);
    }
    
    /**
     * Get all logs for a specific account
     */
    public List<LogEntity> getLogsByAccountNumber(String accountNumber) {
        return logRepository.findByAccount_AccountNumberOrderByCreatedAtDesc(accountNumber);
    }
    
    /**
     * Get logs by transaction ID
     */
    public List<LogEntity> getLogsByTransactionId(String transactionId) {
        return logRepository.findByTransactionIdOrderByCreatedAtDesc(transactionId);
    }
    
    /**
     * Get logs by transaction status
     */
    public List<LogEntity> getLogsByTransactionStatus(String status) {
        return logRepository.findByTransactionStatusOrderByCreatedAtDesc(status);
    }
    
    /**
     * Get logs by event source (KAFKA, DIRECT)
     */
    public List<LogEntity> getLogsByEventSource(String eventSource) {
        return logRepository.findByEventSourceOrderByCreatedAtDesc(eventSource);
    }
    
    /**
     * Get recent Kafka transaction logs
     */
    public List<LogEntity> getRecentKafkaLogs(int limit) {
        return logRepository.findTop10ByEventSourceOrderByCreatedAtDesc("KAFKA");
    }
}