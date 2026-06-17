package com.example.notification_service.service;
import com.example.notification_service.entity.NotificationEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Service
@Slf4j
public class NotificationChannelService {
    
    public boolean sendNotification(NotificationEntity notification) {
        try {
            switch (notification.getChannel()) {
                case "EMAIL":
                    return sendEmail(notification);
                case "SMS":
                    return sendSms(notification);
                case "PUSH":
                    return sendPushNotification(notification);
                default:
                    log.warn("Unknown notification channel: {}", notification.getChannel());
                    return false;
            }
        } catch (Exception e) {
            log.error("Error sending notification via {}: {}", notification.getChannel(), e.getMessage(), e);
            return false;
        }
    }
    private boolean sendEmail(NotificationEntity notification) {
        // Simulate email sending
        log.info("Sending EMAIL notification: {} - {} to user: {}", 
            notification.getTitle(), notification.getMessage(), notification.getUserId());
        try {
            Thread.sleep(100); 
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    private boolean sendSms(NotificationEntity notification) {
        log.info("Sending SMS notification: {} - {} to user: {}", 
            notification.getTitle(), notification.getMessage(), notification.getUserId()); 
        try {
            Thread.sleep(150);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    private boolean sendPushNotification(NotificationEntity notification) {
        log.info("Sending PUSH notification: {} - {} to user: {}", 
            notification.getTitle(), notification.getMessage(), notification.getUserId());
        try {
            Thread.sleep(50);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}