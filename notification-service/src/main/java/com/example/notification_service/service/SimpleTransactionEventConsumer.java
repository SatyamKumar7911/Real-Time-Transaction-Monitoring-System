package com.example.notification_service.service;
import com.example.notification_service.dto.TransactionEvent;
import com.example.notification_service.event.AccountEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleTransactionEventConsumer {
    private final SimpleNotificationService notificationService;
    
    // DISABLED: KafkaEventListener.java handles all event consumption and publishing
    // This class was causing duplicate consumers with same group ID
    // which prevented KafkaEventListener from receiving events
    
    //@KafkaListener(topics = "transaction-events", groupId = "notification-service-group")
    public void handleTransactionEvent(TransactionEvent event) {
        try {
            log.info("Received transaction event: {}", event.getEventId());
            log.info("Transaction details - Type: {}, Amount: {}, User: {}, Account: {}", 
                    event.getEventType(), event.getAmount(), event.getUserId(), event.getAccountNumber());
            
            // Create proper notification through service
            createNotificationForTransactionEvent(event);
            
        } catch (Exception e) {
            log.error("Error processing transaction event: {} - Error: {}", 
                    event.getEventId(), e.getMessage(), e);
        }
    }
    //@KafkaListener(topics = "account-events", groupId = "notification-service-group")
    public void handleAccountEvent(AccountEvent event) {
        try {
            log.info("🔍 [CDC] Received account event: {}", event.getEventType());
            log.info("Account details - Type: {}, User: {}, Account: {}", 
                    event.getEventType(), event.getUserId(), event.getAccountNumber());
            
            createNotificationForAccountEvent(event);
            
        } catch (Exception e) {
            log.error("Error processing account event: {} - Error: {}", 
                    event.getEventType(), e.getMessage(), e);
        }
    }

    private void createNotificationForTransactionEvent(TransactionEvent event) {
        try {
            notificationService.createNotificationFromTransactionEvent(
                    event.getEventType(),
                    event.getUserId(),
                    event.getTransactionId(),
                    event.getAccountNumber(),
                    event.getAmount(),
                    event.getDescription() != null ? event.getDescription() : ""
            );
            
            log.info("Notification created for transaction event: {}", event.getEventId());
            
        } catch (Exception e) {
            log.error("Failed to create notification for transaction event: {}", 
                    event.getEventId(), e);
        }
    }

    private void createNotificationForAccountEvent(AccountEvent event) {
        try {
            String cdcIndicator = event.getEventType() != null && event.getEventType().endsWith("_VIA_CDC") ? "🔍 [CDC] " : "";
            String message;
            
            switch (event.getEventType()) {
                case "USER_CREATED_VIA_CDC":
                    message = String.format("%s👤 Welcome! Your user account has been created.", cdcIndicator);
                    break;
                case "USER_UPDATED_VIA_CDC":
                    message = String.format("%s👤 Your user profile has been updated.", cdcIndicator);
                    break;
                case "USER_DELETED_VIA_CDC":
                    message = String.format("%s👤 Your user account has been deleted.", cdcIndicator);
                    break;
                case "BALANCE_UPDATED_VIA_CDC":
                    message = String.format("%s💰 Balance updated. New balance: $%.2f", cdcIndicator, event.getBalance());
                    break;
                case "ACCOUNT_DELETED_VIA_CDC":
                    message = String.format("%s❌ Account has been deleted.", cdcIndicator);
                    break;
                default:
                    message = String.format("%sAccount event: %s", cdcIndicator, event.getEventType());
            }
            
            notificationService.sendNotification(message, "user@example.com");
            log.info("{}Notification created for account event: {}", cdcIndicator, event.getEventType());
            
        } catch (Exception e) {
            log.error("Failed to create notification for account event: {}", 
                    event.getEventType(), e);
        }
    }
}