package com.example.notification_service.event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String notificationId;
    private String userId;
    private String accountNumber;
    private String notificationType; // EMAIL, SMS, PUSH, ALERT
    private String title;
    private String message;
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL
    private LocalDateTime timestamp;
    private String status; // PENDING, SENT, FAILED
    private String channel; // EMAIL, SMS, PUSH_NOTIFICATION
}