package com.example.notification_service.listener;

import com.example.notification_service.service.SimpleNotificationService;
import com.example.notification_service.service.NotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventListener {

    private final SimpleNotificationService notificationService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "account-events", groupId = "notification-service-group")
    public void handleAccountEvent(Map<String, Object> accountEvent) {
        try {
            log.info("========== ACCOUNT EVENT RECEIVED ==========");
            log.info("Event data: {}", accountEvent);
            
            String eventType = (String) accountEvent.get("eventType");
            Object userIdObj = accountEvent.get("userId");
            String userId = userIdObj != null ? userIdObj.toString() : "unknown";
            String accountNumber = (String) accountEvent.get("accountNumber");
            
            log.info("Extracted - eventType: {}, userId: {}, accountNumber: {}", eventType, userId, accountNumber);
            
            // Check if event is from CDC (direct database change)
            boolean isCdcEvent = eventType != null && eventType.endsWith("_VIA_CDC");
            String cdcIndicator = isCdcEvent ? "[CDC] " : "";
            
            String message = switch (eventType) {
                case "ACCOUNT_CREATED", "ACCOUNT_CREATED_VIA_CDC" -> 
                    String.format("%sNew account created! Account Number: %s", cdcIndicator, accountNumber);
                case "USER_CREATED", "USER_CREATED_VIA_CDC" ->
                    String.format("%sWelcome! Your user account has been created successfully.", cdcIndicator);
                case "BALANCE_UPDATED", "BALANCE_UPDATED_VIA_CDC" -> {
                    Object balanceObj = accountEvent.get("balance");
                    BigDecimal balance = balanceObj != null ? new BigDecimal(balanceObj.toString()) : BigDecimal.ZERO;
                    Object transactionIdObj = accountEvent.get("transactionId");
                    String transactionId = transactionIdObj != null ? transactionIdObj.toString() : "N/A";
                    yield String.format("%sBalance updated for account %s. New balance: $%.2f (Transaction: %s)", 
                        cdcIndicator, accountNumber, balance, transactionId);
                }
                case "ACCOUNT_DELETED_VIA_CDC" ->
                    String.format("[CDC] Account deleted: %s", accountNumber);
                case "USER_UPDATED_VIA_CDC" ->
                    String.format("[CDC] User information updated for user: %s", userId);
                case "USER_DELETED_VIA_CDC" ->
                    String.format("[CDC] User account deleted: %s", userId);
                default ->
                    String.format("%sAccount update: %s for account %s", cdcIndicator, eventType, accountNumber);
            };
            
            if (isCdcEvent) {
                log.info("[CDC] CDC Event detected: {} - Processing notification", eventType);
            }
            
            log.info("Calling notificationService.sendNotification with message: {}", message);
            notificationService.sendNotification(message, userId);
            log.info("notificationService.sendNotification completed successfully");
            
            // Publish notification event
            log.info(">>> ABOUT TO CALL publishNotificationEvent() <<<");
            publishNotificationEvent("ACCOUNT_NOTIFICATION_SENT", userId, accountNumber, message, eventType);
            log.info(">>> publishNotificationEvent() CALL COMPLETED <<<");
            
        } catch (Exception e) {
            log.error("ERROR processing account event - Exception: {}", e.getMessage(), e);
        }
    }
    
    @KafkaListener(topics = "transaction-events", groupId = "notification-service-group")
    public void handleTransactionEvent(Map<String, Object> transactionEvent) {
        try {
            log.info("💰 Received TRANSACTION EVENT: {}", transactionEvent);
            
            String eventType = (String) transactionEvent.get("eventType");
            String transactionId = (String) transactionEvent.get("transactionId");
            String toAccount = (String) transactionEvent.get("toAccount");
            String fromAccount = (String) transactionEvent.get("fromAccount");
            Object amountObj = transactionEvent.get("amount");
            String status = (String) transactionEvent.get("status");
            
            BigDecimal amount = BigDecimal.ZERO;
            if (amountObj != null) {
                amount = new BigDecimal(amountObj.toString());
            }
            
            // Check if event is from CDC (direct database change)
            boolean isCdcEvent = eventType != null && eventType.endsWith("_VIA_CDC");
            String cdcIndicator = isCdcEvent ? "🔍 [CDC] " : "";
            
            String message = switch (eventType) {
                case "TRANSACTION_INITIATED" -> 
                    String.format("%s🔄 Transaction initiated: $%.2f to account %s (ID: %s)", 
                        cdcIndicator, amount, toAccount != null ? toAccount : "N/A", transactionId);
                case "TRANSACTION_COMPLETED" -> 
                    String.format("%s✅ Transaction completed: $%.2f - Status: %s (ID: %s)", 
                        cdcIndicator, amount, status, transactionId);
                case "TRANSFER_INITIATED" -> 
                    String.format("%s🔀 Transfer initiated: $%.2f from %s to %s (ID: %s)", 
                        cdcIndicator, amount, fromAccount, toAccount, transactionId);
                case "TRANSFER_COMPLETED" -> 
                    String.format("%s✅ Transfer completed: $%.2f - Status: %s (ID: %s)", 
                        cdcIndicator, amount, status, transactionId);
                case "TRANSACTION_LOGGED_VIA_CDC" ->
                    String.format("🔍 [CDC] 📋 Transaction logged: $%.2f (ID: %s)", amount, transactionId);
                default ->
                    String.format("%s💳 Transaction update: %s - $%.2f (ID: %s)", 
                        cdcIndicator, eventType, amount, transactionId);
            };
            
            if (isCdcEvent) {
                log.info("🔍 CDC Event detected: {} - Processing notification", eventType);
            }
            
            // Use toAccount as recipient for notification routing
            String recipient = toAccount != null ? toAccount : (fromAccount != null ? fromAccount : "system");
            notificationService.sendNotification(message, recipient);
            
            // Publish notification event
            publishNotificationEvent("TRANSACTION_NOTIFICATION_SENT", recipient, transactionId, message, eventType);
            
        } catch (Exception e) {
            log.error("❌ Error processing transaction event: {}", e.getMessage(), e);
        }
    }
    
    private void publishNotificationEvent(String eventType, String userId, String referenceId, 
                                         String message, String sourceEventType) {
        try {
            log.info("========== INSIDE publishNotificationEvent() ==========");
            log.info("Parameters - eventType: {}, userId: {}, referenceId: {}", eventType, userId, referenceId);
            log.info("Parameters - message: {}, sourceEventType: {}", message, sourceEventType);
            
            Map<String, Object> notificationEvent = new HashMap<>();
            notificationEvent.put("eventType", eventType);
            notificationEvent.put("userId", userId);
            notificationEvent.put("referenceId", referenceId);
            notificationEvent.put("message", message);
            notificationEvent.put("sourceEventType", sourceEventType);
            notificationEvent.put("timestamp", LocalDateTime.now().toString());
            notificationEvent.put("status", "SENT");
            
            log.info("Created notification event map: {}", notificationEvent);
            log.info("KafkaTemplate instance: {}", kafkaTemplate != null ? "VALID" : "NULL");
            log.info(">>> CALLING kafkaTemplate.send('notification-events', ...) <<<");
            
            kafkaTemplate.send("notification-events", userId, notificationEvent);
            
            log.info(">>> kafkaTemplate.send() COMPLETED SUCCESSFULLY <<<");
            log.info("Published notification event to notification-events topic: {}", eventType);
            
        } catch (Exception e) {
            log.error("========== FAILED to publish notification event ==========");
            log.error("Exception type: {}", e.getClass().getName());
            log.error("Exception message: {}", e.getMessage(), e);
        }
    }
}