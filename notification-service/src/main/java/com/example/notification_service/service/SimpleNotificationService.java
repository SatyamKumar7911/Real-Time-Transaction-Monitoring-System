package com.example.notification_service.service;
import com.example.notification_service.dto.NotificationRequest;
import com.example.notification_service.dto.NotificationResponse;
import com.example.notification_service.entity.Notification;
import com.example.notification_service.entity.NotificationChannel;
import com.example.notification_service.entity.NotificationStatus;
import com.example.notification_service.entity.NotificationType;
import com.example.notification_service.event.NotificationEventPublisher;
import com.example.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleNotificationService {
    
    private final NotificationRepository notificationRepository;
    private final NotificationEventPublisher eventPublisher;
    public void sendNotification(String message, String recipient) {
        log.info("Sending notification to {}: {}", recipient, message);
        Notification notification = Notification.builder()
                .eventId(UUID.randomUUID().toString())
                .recipient(recipient)
                .message(message)
                .notificationType(NotificationType.TRANSACTION_SUCCESS) // Use existing enum value
                .channel(NotificationChannel.EMAIL)
                .status(NotificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        Notification saved = notificationRepository.save(notification);
        log.info("Notification saved with ID: {}", saved.getId());
        processNotificationDelivery(saved);
    }
    public void processNotification(String eventId, String eventType, String userId) {
        log.info("Processing notification - Event: {}, Type: {}, User: {}", 
                eventId, eventType, userId);
        
        String message = String.format("Event %s occurred for user %s", eventType, userId);
        sendNotification(message, userId);
    }
    public NotificationResponse createNotificationFromAccountEvent(String eventType, String userId, 
            String accountNumber, String details) {
        log.info("Creating notification from account event: {} for user: {}", eventType, userId);
        
        Notification notification = Notification.builder()
                .eventId(UUID.randomUUID().toString())
                .userId(userId)
                .accountNumber(accountNumber)
                .notificationType(getNotificationTypeFromEvent(eventType))
                .channel(NotificationChannel.EMAIL)
                .recipient(userId + "@example.com") // Default email pattern
                .message(generateAccountMessage(eventType, accountNumber, details))
                .status(NotificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        
        Notification saved = notificationRepository.save(notification);
        processNotificationDelivery(saved);
        
        return convertToResponse(saved);
    }
    public NotificationResponse createNotificationFromTransactionEvent(String eventType, String userId, 
            String transactionId, String accountNumber, BigDecimal amount, String details) {
        log.info("Creating notification from transaction event: {} for user: {}", eventType, userId);
        
        Notification notification = Notification.builder()
                .eventId(UUID.randomUUID().toString())
                .userId(userId)
                .accountNumber(accountNumber)
                .transactionId(transactionId)
                .transactionAmount(amount) // Use correct field name
                .notificationType(getNotificationTypeFromEvent(eventType))
                .channel(NotificationChannel.EMAIL)
                .recipient(userId + "@example.com")
                .message(generateTransactionMessage(eventType, accountNumber, amount, details))
                .status(NotificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        
        Notification saved = notificationRepository.save(notification);
        processNotificationDelivery(saved);
        
        return convertToResponse(saved);
    }
    
    public List<NotificationResponse> getAllNotifications() {
        log.info("Fetching all notifications");
        return notificationRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    
    public NotificationResponse createNotification(NotificationRequest request) {
        log.info("Creating notification for user: {}", request.getUserId());
        
        Notification notification = Notification.builder()
                .eventId(UUID.randomUUID().toString())
                .userId(request.getUserId())
                .recipient(request.getAccountNumber())
                .message(request.getMessage())
                .notificationType(mapStringToNotificationType(request.getNotificationType()))
                .channel(mapStringToNotificationChannel(request.getChannel()))
                .status(NotificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        
        Notification saved = notificationRepository.save(notification);
        log.info("Notification created with ID: {}", saved.getId());
        
        // Publish notification sent event to Kafka
        eventPublisher.publishNotificationSent(
            saved.getId(),
            saved.getUserId(),
            saved.getMessage(),
            saved.getChannel().toString()
        );
        
        return convertToResponse(saved);
    }
    
    public List<NotificationResponse> getNotificationsByUserId(String userId) {
        log.info("Fetching notifications for user: {}", userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    public Optional<NotificationResponse> getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .map(this::convertToResponse);
    }
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId)
                .ifPresent(notification -> {
                    notification.setStatus(NotificationStatus.DELIVERED); // Use existing status
                    notification.setReadAt(LocalDateTime.now());
                    notificationRepository.save(notification);
                    
                    // Publish notification read event to Kafka
                    eventPublisher.publishNotificationRead(
                        notification.getId(),
                        notification.getUserId()
                    );
                    
                    log.info("Notification {} marked as read", notificationId);
                });
    }
    private void processNotificationDelivery(Notification notification) {
        try {
            log.info("Delivering notification: {} via {}", 
                    notification.getMessage(), notification.getChannel());
     
            notification.setStatus(NotificationStatus.DELIVERED);
            notification.setSentAt(LocalDateTime.now()); // Use correct setter
            notificationRepository.save(notification);
            
            // Publish notification delivered event to Kafka
            eventPublisher.publishNotificationDelivered(
                notification.getId(), 
                notification.getUserId(), 
                notification.getChannel().toString()
            );
            
            log.info("Notification delivered successfully: {}", notification.getId());
            
        } catch (Exception e) {
            log.error("Failed to deliver notification: {}", notification.getId(), e);
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            notificationRepository.save(notification);
            
            // Publish notification failed event to Kafka
            eventPublisher.publishNotificationFailed(
                notification.getId(),
                notification.getUserId(),
                notification.getChannel().toString(),
                e.getMessage()
            );
        }
    }
    
    private NotificationType getNotificationTypeFromEvent(String eventType) {
        return switch (eventType.toUpperCase()) {
            case "ACCOUNT_CREATED" -> NotificationType.LOGIN_ALERT;
            case "BALANCE_UPDATED" -> NotificationType.ACCOUNT_BALANCE_LOW;
            case "TRANSACTION_COMPLETED" -> NotificationType.TRANSACTION_SUCCESS;
            case "TRANSACTION_FAILED" -> NotificationType.TRANSACTION_FAILED;
            case "CREDIT" -> NotificationType.TRANSFER_RECEIVED;
            case "DEBIT" -> NotificationType.CARD_TRANSACTION;
            default -> NotificationType.TRANSACTION_SUCCESS;
        };
    }
    
    private String generateAccountMessage(String eventType, String accountNumber, String details) {
        return switch (eventType.toUpperCase()) {
            case "ACCOUNT_CREATED" -> String.format("New account created: %s. %s", accountNumber, details);
            case "BALANCE_UPDATED" -> String.format("Balance updated for account %s. %s", accountNumber, details);
            default -> String.format("Account event: %s for account %s. %s", eventType, accountNumber, details);
        };
    }
    
    private String generateTransactionMessage(String eventType, String accountNumber, BigDecimal amount, String details) {
        return switch (eventType.toUpperCase()) {
            case "TRANSACTION_COMPLETED" -> String.format("Transaction completed: $%.2f for account %s. %s", 
                    amount, accountNumber, details);
            case "TRANSACTION_FAILED" -> String.format("Transaction failed: $%.2f for account %s. %s", 
                    amount, accountNumber, details);
            case "CREDIT" -> String.format("Credit transaction: $%.2f received in account %s. %s", 
                    amount, accountNumber, details);
            case "DEBIT" -> String.format("Debit transaction: $%.2f from account %s. %s", 
                    amount, accountNumber, details);
            default -> String.format("Transaction event: %s - $%.2f for account %s. %s", 
                    eventType, amount, accountNumber, details);
        };
    }
    
    private NotificationResponse convertToResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setNotificationId(notification.getEventId());
        response.setUserId(notification.getUserId());
        response.setAccountNumber(notification.getAccountNumber());
        response.setNotificationType(notification.getNotificationType().toString());
        response.setTitle(notification.getSubject());
        response.setMessage(notification.getMessage());
        response.setPriority(notification.getPriority() != null ? notification.getPriority().toString() : "1");
        response.setTimestamp(notification.getCreatedAt());
        response.setStatus(notification.getStatus().toString());
        response.setChannel(notification.getChannel().toString());
        response.setRetryCount(notification.getRetryCount());
        response.setSentAt(notification.getSentAt());
        response.setErrorMessage(notification.getErrorMessage());
        return response;
    }
    
    private NotificationType mapStringToNotificationType(String type) {
        if (type == null) return NotificationType.TRANSACTION_SUCCESS;
        try {
            return NotificationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown notification type: {}, defaulting to TRANSACTION_SUCCESS", type);
            return NotificationType.TRANSACTION_SUCCESS;
        }
    }
    
    private NotificationChannel mapStringToNotificationChannel(String channel) {
        if (channel == null) return NotificationChannel.EMAIL;
        try {
            return NotificationChannel.valueOf(channel.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown notification channel: {}, defaulting to EMAIL", channel);
            return NotificationChannel.EMAIL;
        }
    }
}