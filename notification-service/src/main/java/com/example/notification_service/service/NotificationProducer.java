package com.example.notification_service.service;
import com.example.notification_service.dto.TransactionEvent;
import com.example.notification_service.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public void sendNotificationStatus(Notification notification) {
        try {
            String topic = "notification-status";
            String key = notification.getEventId() + "-" + notification.getChannel().name();
            
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, notification);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent notification status to topic: {} with key: {} and offset: {}",
                            topic, key, result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send notification status for key: {} - Error: {}", key, ex.getMessage());
                }
            });
            
        } catch (Exception e) {
            log.error("Error sending notification status: {}", e.getMessage(), e);
        }
    }
    public void sendFailedNotificationForRetry(Notification notification) {
        try {
            String topic = "failed-notifications";
            String key = notification.getEventId();
            
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, notification);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Sent failed notification to retry topic: {} with key: {} and offset: {}",
                            topic, key, result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send notification to retry topic for key: {} - Error: {}", key, ex.getMessage());
                }
            });
            
        } catch (Exception e) {
            log.error("Error sending failed notification for retry: {}", e.getMessage(), e);
        }
    }
    public void sendNotificationMetrics(String eventType, String userId, String channel, String status) {
        try {
            String topic = "notification-metrics";
            String key = userId;
            
            NotificationMetric metric = NotificationMetric.builder()
                    .eventType(eventType)
                    .userId(userId)
                    .channel(channel)
                    .status(status)
                    .timestamp(System.currentTimeMillis())
                    .build();
            
            kafkaTemplate.send(topic, key, metric);
            
        } catch (Exception e) {
            log.error("Error sending notification metrics: {}", e.getMessage(), e);
        }
    }
    public void sendDeliveryConfirmation(String notificationId, String channel, boolean delivered) {
        try {
            String topic = "delivery-confirmations";
            String key = notificationId;
            
            DeliveryConfirmation confirmation = DeliveryConfirmation.builder()
                    .notificationId(notificationId)
                    .channel(channel)
                    .delivered(delivered)
                    .timestamp(System.currentTimeMillis())
                    .build();
            
            kafkaTemplate.send(topic, key, confirmation);
            
        } catch (Exception e) {
            log.error("Error sending delivery confirmation: {}", e.getMessage(), e);
        }
    }
    public void sendEmergencyAlert(TransactionEvent event, String alertType) {
        try {
            String topic = "emergency-alerts";
            String key = event.getUserId();
            
            EmergencyAlert alert = EmergencyAlert.builder()
                    .eventId(event.getEventId())
                    .userId(event.getUserId())
                    .alertType(alertType)
                    .transactionId(event.getTransactionId())
                    .amount(event.getAmount())
                    .timestamp(event.getTimestamp())
                    .description(event.getDescription())
                    .riskScore(event.getRiskScore())
                    .build();
            
            kafkaTemplate.send(topic, key, alert);
            
        } catch (Exception e) {
            log.error("Error sending emergency alert: {}", e.getMessage(), e);
        }
    }
    @lombok.Builder
    @lombok.Data
    public static class NotificationMetric {
        private String eventType;
        private String userId;
        private String channel;
        private String status;
        private Long timestamp;
    }

    @lombok.Builder
    @lombok.Data
    public static class EmergencyAlert {
        private String eventId;
        private String userId;
        private String alertType;
        private String transactionId;
        private java.math.BigDecimal amount;
        private java.time.LocalDateTime timestamp;
        private String description;
        private Double riskScore;
    }

    @lombok.Builder
    @lombok.Data
    public static class DeliveryConfirmation {
        private String notificationId;
        private String channel;
        private Boolean delivered;
        private Long timestamp;
        private String errorMessage;
    }
}