package com.example.accountservice.repository;

import com.example.accountservice.entity.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LogRepository extends JpaRepository<LogEntity, Long> {
    
    /**
     * Find logs by account number ordered by creation date descending
     */
    List<LogEntity> findByAccount_AccountNumberOrderByCreatedAtDesc(String accountNumber);
    
    /**
     * Find logs by transaction ID ordered by creation date descending
     */
    List<LogEntity> findByTransactionIdOrderByCreatedAtDesc(String transactionId);
    
    /**
     * Find logs by transaction status ordered by creation date descending
     */
    List<LogEntity> findByTransactionStatusOrderByCreatedAtDesc(String transactionStatus);
    
    /**
     * Find logs by event source ordered by creation date descending
     */
    List<LogEntity> findByEventSourceOrderByCreatedAtDesc(String eventSource);
    
    /**
     * Find top N logs by event source ordered by creation date descending
     */
    @Query("SELECT l FROM LogEntity l WHERE l.eventSource = :eventSource ORDER BY l.createdAt DESC LIMIT :limit")
    List<LogEntity> findTop10ByEventSourceOrderByCreatedAtDesc(@Param("eventSource") String eventSource);
    
    /**
     * Find logs by account ID
     */
    List<LogEntity> findByAccount_IdOrderByCreatedAtDesc(Long accountId);
    
    /**
     * Find logs containing specific action
     */
    List<LogEntity> findByActionContainingOrderByCreatedAtDesc(String action);
}
