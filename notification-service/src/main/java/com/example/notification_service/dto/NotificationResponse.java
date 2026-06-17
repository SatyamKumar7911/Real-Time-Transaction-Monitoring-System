package com.example.notification_service.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private String notificationId;
    private String userId;
    private String accountNumber;
    private String notificationType;
    private String title;
    private String message;
    private String priority;
    private LocalDateTime timestamp;
    private String status;
    private String channel;
    private Integer retryCount;
    private LocalDateTime sentAt;
    private String errorMessage;
}