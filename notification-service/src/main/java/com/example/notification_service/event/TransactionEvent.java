package com.example.notification_service.event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    private String eventType; // TRANSACTION_INITIATED, TRANSACTION_COMPLETED, TRANSACTION_FAILED, etc.
    private String transactionId;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime timestamp;
    private String status;
    private String description;
}