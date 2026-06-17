package com.example.notification_service.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Account number is required")
    private String accountNumber;
    
    private String notificationType; // EMAIL, SMS, PUSH, ALERT
    private String title;
    
    @NotBlank(message = "Message is required")
    private String message;
    
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL
    private String channel; // EMAIL, SMS, PUSH_NOTIFICATION
}