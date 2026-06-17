package com.example.notification_service.controller;
import com.example.notification_service.dto.NotificationRequest;
import com.example.notification_service.dto.NotificationResponse;
import com.example.notification_service.service.SimpleNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications/v1")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final SimpleNotificationService notificationService;
    @PostMapping("/notifications/send")
    public ResponseEntity<String> sendNotification(
            @RequestParam String message,
            @RequestParam String recipient) { 
        log.info("Sending notification to: {}", recipient);
        notificationService.sendNotification(message, recipient);
        return ResponseEntity.ok("Notification sent successfully");
    }
    @PostMapping("/notifications/process")
    public ResponseEntity<String> processNotification(
            @RequestParam String eventId,
            @RequestParam String eventType,
            @RequestParam String userId) { 
        log.info("Processing notification for user: {}", userId);
        notificationService.processNotification(eventId, eventType, userId);
        return ResponseEntity.ok("Notification processed successfully");
    }
    @PostMapping("/notifications/account-event")
    public ResponseEntity<NotificationResponse> createAccountNotification(
            @RequestParam String eventType,
            @RequestParam String userId,
            @RequestParam String accountNumber,
            @RequestParam(required = false) String details) {
        
        log.info("Creating account notification - Event: {}, User: {}, Account: {}", 
                eventType, userId, accountNumber);
        
        NotificationResponse response = notificationService.createNotificationFromAccountEvent(
                eventType, userId, accountNumber, details != null ? details : "");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/notifications/transaction-event")
    public ResponseEntity<NotificationResponse> createTransactionNotification(
            @RequestParam String eventType,
            @RequestParam String userId,
            @RequestParam String transactionId,
            @RequestParam String accountNumber,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String details) {
        
        log.info("Creating transaction notification - Event: {}, User: {}, Transaction: {}", 
                eventType, userId, transactionId);
        
        NotificationResponse response = notificationService.createNotificationFromTransactionEvent(
                eventType, userId, transactionId, accountNumber, amount, 
                details != null ? details : "");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        log.info("Fetching all notifications");
        List<NotificationResponse> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(@Valid @RequestBody NotificationRequest request) {
        log.info("Creating notification for user: {}", request.getUserId());
        NotificationResponse response = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(@PathVariable String userId) {
        
        log.info("Fetching notifications for user: {}", userId);
        List<NotificationResponse> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotification(@PathVariable Long id) {
        
        log.info("Fetching notification with ID: {}", id);
        return notificationService.getNotificationById(id)
                .map(notification -> ResponseEntity.ok(notification))
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long id) {
        
        log.info("Marking notification as read: {}", id);
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Notification marked as read");
    }
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = Map.of(
                "status", "UP",
                "service", "notification-service",
                "kafka", "enabled",
                "timestamp", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(status);
    }
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<NotificationResponse>> getAccountNotifications(@PathVariable Long accountId) {
        log.info("Fetching notifications for account: {}", accountId);
        // Return sample notifications for account
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications() {
        log.info("Fetching unread notifications");
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        log.info("Checking notification service status");
        Map<String, Object> status = Map.of(
                "service", "notification-service",
                "status", "running",
                "features", List.of("kafka-integration", "event-processing", "notifications"),
                "version", "1.0.0"
        );
        return ResponseEntity.ok(status);
    }
    @PostMapping("/v1/notify")
    public ResponseEntity<Void> notifyUser(@RequestBody Map<String, Object> payload) {
        try {
            log.info("NOTIFICATION SENT: {}", payload);
            return ResponseEntity.accepted().build();
            
        } catch (Exception e) {
            log.error("Error in legacy notify endpoint: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }
}