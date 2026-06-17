package com.example.notification_service.service;

import com.example.notification_service.dto.*;
import com.example.notification_service.entity.Notification;
import com.example.notification_service.entity.NotificationType;
import com.example.notification_service.entity.NotificationStatus;
import com.example.notification_service.entity.NotificationChannel;
import com.example.notification_service.event.NotificationEventPublisher;
import com.example.notification_service.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SimpleNotificationService
 * Simplified version focusing on core functionality
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Notification Service Tests")
class SimpleNotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationEventPublisher eventPublisher;

    @InjectMocks
    private SimpleNotificationService notificationService;

    private Notification testNotification;
    private NotificationRequest testRequest;

    @BeforeEach
    void setUp() {
        testNotification = Notification.builder()
                .id(1L)
                .message("Test notification message")
                .userId("user123")
                .notificationType(NotificationType.TRANSACTION_SUCCESS)
                .channel(NotificationChannel.EMAIL)
                .status(NotificationStatus.PENDING)
                .build();

        testRequest = new NotificationRequest();
        testRequest.setMessage("New notification message");
        testRequest.setUserId("user456");
        testRequest.setNotificationType("ACCOUNT_CREATED");
    }

    @Test
    @DisplayName("Should create notification successfully")
    void shouldCreateNotificationSuccessfully() {
        // Given
        Notification savedNotification = Notification.builder()
                .id(2L)
                .message("New notification message")
                .userId("user456")
                .notificationType(NotificationType.TRANSACTION_SUCCESS)
                .channel(NotificationChannel.EMAIL)
                .status(NotificationStatus.PENDING)
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        // When
        NotificationResponse result = notificationService.createNotification(testRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("New notification message");
        assertThat(result.getUserId()).isEqualTo("user456");

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should get all notifications successfully")
    void shouldGetAllNotificationsSuccessfully() {
        // Given
        Notification notification2 = Notification.builder()
                .id(2L)
                .message("Second notification")
                .userId("user789")
                .notificationType(NotificationType.TRANSACTION_SUCCESS)
                .channel(NotificationChannel.EMAIL)
                .status(NotificationStatus.PENDING)
                .build();

        when(notificationRepository.findAll()).thenReturn(Arrays.asList(testNotification, notification2));

        // When
        List<NotificationResponse> result = notificationService.getAllNotifications();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMessage()).isEqualTo("Test notification message");
        assertThat(result.get(1).getMessage()).isEqualTo("Second notification");

        verify(notificationRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should send notification successfully")
    void shouldSendNotificationSuccessfully() {
        // Given
        String message = "Important notification";
        String recipient = "user@example.com";
        
        Notification mockSavedNotification = Notification.builder()
                .id(1L)
                .message(message)
                .recipient(recipient)
                .notificationType(NotificationType.TRANSACTION_SUCCESS)
                .channel(NotificationChannel.EMAIL)
                .status(NotificationStatus.PENDING)
                .build();
        
        when(notificationRepository.save(any(Notification.class))).thenReturn(mockSavedNotification);

        // When
        notificationService.sendNotification(message, recipient);

        // Then
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should process notification successfully")
    void shouldProcessNotificationSuccessfully() {
        // Given
        String eventId = "event123";
        String eventType = "ACCOUNT_CREATED";
        String userId = "user456";
        
        Notification savedNotification = Notification.builder()
                .id(3L)
                .message("Event ACCOUNT_CREATED occurred for user user456")
                .userId("user456")
                .notificationType(NotificationType.TRANSACTION_SUCCESS)
                .channel(NotificationChannel.EMAIL)
                .status(NotificationStatus.PENDING)
                .build();
                
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        // When
        notificationService.processNotification(eventId, eventType, userId);

        // Then
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should handle null notification request")
    void shouldHandleNullNotificationRequest() {
        // When & Then
        assertThatThrownBy(() -> notificationService.createNotification(null))
                .isInstanceOf(RuntimeException.class);

        verifyNoInteractions(notificationRepository);
    }
}