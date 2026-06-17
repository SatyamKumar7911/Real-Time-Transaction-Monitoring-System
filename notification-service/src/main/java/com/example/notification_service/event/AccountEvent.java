package com.example.notification_service.event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountEvent {
    private String eventType; // ACCOUNT_CREATED, BALANCE_UPDATED, etc.
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private Long userId;
    private LocalDateTime timestamp;
    private String transactionId;
}