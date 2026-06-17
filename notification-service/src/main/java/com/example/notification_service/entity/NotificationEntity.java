package com.example.notification_service.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEntity {   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "notification_id", unique = true, nullable = false)
    private String notificationId;
    
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "account_number")
    private String accountNumber;
    
    @Column(name = "notification_type", nullable = false)
    private String notificationType; // EMAIL, SMS, PUSH, ALERT
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "message", nullable = false, length = 1000)
    private String message;
    
    @Column(name = "priority", nullable = false)
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "status", nullable = false)
    private String status; // PENDING, SENT, FAILED
    
    @Column(name = "channel")
    private String channel; // EMAIL, SMS, PUSH_NOTIFICATION
    
    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "error_message")
    private String errorMessage;
}