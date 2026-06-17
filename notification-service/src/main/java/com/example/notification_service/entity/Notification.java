package com.example.notification_service.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "event_id", unique = true)
    private String eventId;
    
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "account_number")
    private String accountNumber;
    
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType notificationType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private NotificationChannel channel;
    
    @Column(name = "recipient")
    private String recipient; // email, phone, or device token
    
    @Column(name = "subject")
    private String subject;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "transaction_amount")
    private BigDecimal transactionAmount;
    
    @Column(name = "balance_after")
    private BigDecimal balanceAfter;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private NotificationStatus status;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "priority")
    private Integer priority = 1; // 1=LOW, 2=NORMAL, 3=HIGH, 4=CRITICAL
    
    @Column(name = "notification_id", unique = true)
    private String notificationId;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = NotificationStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}