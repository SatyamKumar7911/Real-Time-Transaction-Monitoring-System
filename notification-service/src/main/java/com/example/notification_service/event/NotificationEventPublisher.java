package com.example.notification_service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventPublisher {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String NOTIFICATION_TOPIC = "notification-events";
    
    public void publishNotificationSent(Long notificationId, String userId, String message, String channel) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "NOTIFICATION_SENT");
        event.put("notificationId", notificationId);
        event.put("userId", userId);
        event.put("message", message);
        event.put("channel", channel);
        event.put("timestamp", LocalDateTime.now().toString());
        event.put("status", "SENT");
        
        kafkaTemplate.send(NOTIFICATION_TOPIC, notificationId.toString(), event);
        log.info("Published NOTIFICATION_SENT event for notification ID: {}", notificationId);
    }
    
    public void publishNotificationDelivered(Long notificationId, String userId, String channel) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "NOTIFICATION_DELIVERED");
        event.put("notificationId", notificationId);
        event.put("userId", userId);
        event.put("channel", channel);
        event.put("timestamp", LocalDateTime.now().toString());
        event.put("status", "DELIVERED");
        
        kafkaTemplate.send(NOTIFICATION_TOPIC, notificationId.toString(), event);
        log.info("Published NOTIFICATION_DELIVERED event for notification ID: {}", notificationId);
    }
    
    public void publishNotificationFailed(Long notificationId, String userId, String channel, String errorMessage) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "NOTIFICATION_FAILED");
        event.put("notificationId", notificationId);
        event.put("userId", userId);
        event.put("channel", channel);
        event.put("errorMessage", errorMessage);
        event.put("timestamp", LocalDateTime.now().toString());
        event.put("status", "FAILED");
        
        kafkaTemplate.send(NOTIFICATION_TOPIC, notificationId.toString(), event);
        log.info("Published NOTIFICATION_FAILED event for notification ID: {}", notificationId);
    }
    
    public void publishNotificationRead(Long notificationId, String userId) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "NOTIFICATION_READ");
        event.put("notificationId", notificationId);
        event.put("userId", userId);
        event.put("timestamp", LocalDateTime.now().toString());
        event.put("status", "READ");
        
        kafkaTemplate.send(NOTIFICATION_TOPIC, notificationId.toString(), event);
        log.info("Published NOTIFICATION_READ event for notification ID: {}", notificationId);
    }
}