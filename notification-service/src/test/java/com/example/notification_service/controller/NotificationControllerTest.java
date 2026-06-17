package com.example.notification_service.controller;

import com.example.notification_service.dto.*;
import com.example.notification_service.service.SimpleNotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for NotificationController
 * Tests all REST endpoints for notification management
 */
@WebMvcTest(NotificationController.class)
@DisplayName("Notification Controller Tests")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SimpleNotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should send notification successfully")
    void shouldSendNotificationSuccessfully() throws Exception {
        // Given
        String message = "Test notification";
        String recipient = "user@example.com";
        
        doNothing().when(notificationService).sendNotification(anyString(), anyString());

        // When & Then
        mockMvc.perform(post("/api/notifications/send")
                .param("message", message)
                .param("recipient", recipient))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification sent successfully"));
    }

    @Test
    @DisplayName("Should process notification successfully")
    void shouldProcessNotificationSuccessfully() throws Exception {
        // Given
        String eventId = "event123";
        String eventType = "ACCOUNT_CREATED";
        String userId = "user123";
        
        doNothing().when(notificationService).processNotification(anyString(), anyString(), anyString());

        // When & Then
        mockMvc.perform(post("/api/notifications/process")
                .param("eventId", eventId)
                .param("eventType", eventType)
                .param("userId", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification processed successfully"));
    }

    @Test
    @DisplayName("Should create account notification successfully")
    void shouldCreateAccountNotificationSuccessfully() throws Exception {
        // Given
        String eventType = "ACCOUNT_CREATED";
        String userId = "user123";
        String accountNumber = "ACC123456789";
        String details = "New savings account created";
        
        NotificationResponse response = NotificationResponse.builder()
                .notificationId("1")
                .message("Account created notification")
                .status("PENDING")
                .timestamp(LocalDateTime.now())
                .userId(userId)
                .notificationType(eventType)
                .build();
        
        when(notificationService.createNotificationFromAccountEvent(
                anyString(), anyString(), anyString(), anyString())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/notifications/account-event")
                .param("eventType", eventType)
                .param("userId", userId)
                .param("accountNumber", accountNumber)
                .param("details", details))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notificationId").value("1"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    @DisplayName("Should create transaction notification successfully")
    void shouldCreateTransactionNotificationSuccessfully() throws Exception {
        // Given
        String eventType = "TRANSACTION_COMPLETED";
        String userId = "user123";
        String transactionId = "TXN123";
        String amount = "500.00";
        
        NotificationResponse response = NotificationResponse.builder()
                .notificationId("2")
                .message("Transaction completed notification")
                .status("SENT")
                .timestamp(LocalDateTime.now())
                .userId(userId)
                .notificationType(eventType)
                .build();
        
        when(notificationService.createNotificationFromTransactionEvent(
                anyString(), anyString(), anyString(), anyString(), any(), anyString())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/notifications/transaction-event")
                .param("eventType", eventType)
                .param("userId", userId)
                .param("transactionId", transactionId)
                .param("accountNumber", "ACC123456789")
                .param("amount", amount))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notificationId").value("2"))
                .andExpect(jsonPath("$.status").value("SENT"))
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    @DisplayName("Should get all notifications successfully")
    void shouldGetAllNotificationsSuccessfully() throws Exception {
        // Given
        List<NotificationResponse> notifications = Arrays.asList(
                NotificationResponse.builder()
                        .notificationId("1")
                        .message("Account created")
                        .status("DELIVERED")
                        .timestamp(LocalDateTime.now())
                        .userId("user1")
                        .notificationType("ACCOUNT_CREATED")
                        .build(),
                NotificationResponse.builder()
                        .notificationId("2")
                        .message("Transaction completed")
                        .status("SENT")
                        .timestamp(LocalDateTime.now())
                        .userId("user2")
                        .notificationType("TRANSACTION_COMPLETED")
                        .build()
        );
        
        when(notificationService.getAllNotifications()).thenReturn(notifications);

        // When & Then
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].notificationId").value("1"))
                .andExpect(jsonPath("$[1].notificationId").value("2"));
    }

    @Test
    @DisplayName("Should create notification from request body")
    void shouldCreateNotificationFromRequestBody() throws Exception {
        // Given
        NotificationRequest request = new NotificationRequest();
        request.setUserId("user123");
        request.setAccountNumber("account456");
        request.setMessage("Test notification message");
        request.setNotificationType("TEST_EVENT");
        request.setTitle("Test Title");
        request.setPriority("MEDIUM");
        request.setChannel("EMAIL");
        
        NotificationResponse response = NotificationResponse.builder()
                .notificationId("3")
                .message("Test notification message")
                .status("PENDING")
                .timestamp(LocalDateTime.now())
                .userId("user123")
                .notificationType("TEST_EVENT")
                .title("Test Title")
                .priority("MEDIUM")
                .channel("EMAIL")
                .build();
        
        when(notificationService.createNotification(any(NotificationRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notificationId").value("3"))
                .andExpect(jsonPath("$.message").value("Test notification message"))
                .andExpect(jsonPath("$.userId").value("user123"));
    }

    @Test
    @DisplayName("Should return 400 for missing required parameters")
    void shouldReturn400ForMissingRequiredParameters() throws Exception {
        // When & Then - Missing message parameter
        mockMvc.perform(post("/api/notifications/send")
                .param("recipient", "user@example.com"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for invalid notification request")
    void shouldReturn400ForInvalidNotificationRequest() throws Exception {
        // Given - Request with null/empty required fields
        NotificationRequest invalidRequest = new NotificationRequest();
        invalidRequest.setUserId("");
        invalidRequest.setAccountNumber("");
        invalidRequest.setMessage("");

        // When & Then
        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle account event without details parameter")
    void shouldHandleAccountEventWithoutDetailsParameter() throws Exception {
        // Given
        String eventType = "BALANCE_UPDATED";
        String userId = "user456";
        String accountNumber = "ACC987654321";
        
        NotificationResponse response = NotificationResponse.builder()
                .notificationId("4")
                .message("Balance updated notification")
                .status("PENDING")
                .timestamp(LocalDateTime.now())
                .userId(userId)
                .notificationType(eventType)
                .title("Balance Update")
                .priority("MEDIUM")
                .channel("EMAIL")
                .build();
        
        when(notificationService.createNotificationFromAccountEvent(
                anyString(), anyString(), anyString(), eq(""))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/notifications/account-event")
                .param("eventType", eventType)
                .param("userId", userId)
                .param("accountNumber", accountNumber))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notificationId").value("4"))
                .andExpect(jsonPath("$.userId").value(userId));
    }
}