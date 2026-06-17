package com.example.notification_service.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    
    private String eventId;
    private String eventType; // CREDIT, DEBIT, TRANSFER
    private String transactionId;
    private String accountNumber;
    private BigDecimal amount;
    private String description;
    private LocalDateTime timestamp;
    private String status; // SUCCESS, FAILED, PENDING
    private BigDecimal balanceAfter;
    private String sourceAccountNumber; // For transfers
    private String targetAccountNumber; // For transfers
    private String merchantName;
    private String location;
    private String category;
    private Double riskScore; // For fraud detection
    // Additional metadata
    private String userId;
    private String userEmail;
    private String userPhone;
    private String deviceId;
    private String ipAddress;
    // Notification preferences
    private Boolean emailNotificationEnabled;
    private Boolean smsNotificationEnabled;
    private Boolean pushNotificationEnabled; 
    public boolean isHighValueTransaction() {
        return amount != null && amount.compareTo(new BigDecimal("10000")) > 0;
    }
    public boolean isSuspicious() {
        return riskScore != null && riskScore > 0.7;
    }
    public boolean isFailedTransaction() {
        return "FAILED".equals(status);
    }
}