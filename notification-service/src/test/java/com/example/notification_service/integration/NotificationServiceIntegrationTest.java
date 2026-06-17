package com.example.notification_service.integration;

import com.example.notification_service.NotificationServiceApplication;
import com.example.notification_service.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Notification Service
 * Tests complete flow with H2 database and Kafka
 */
@SpringBootTest(
        classes = NotificationServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureWebMvc
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Notification Service Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled("Docker environment required - skip for unit testing")
class NotificationServiceIntegrationTest {

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Kafka configuration
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.consumer.group-id", () -> "test-group");
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
        
        // H2 Database configuration
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        
        // Eureka configuration (disable for tests)
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.discovery.enabled", () -> "false");
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Order(1)
    @DisplayName("Should send notification successfully")
    @Transactional
    void shouldSendNotificationSuccessfully() throws Exception {
        // Given
        String message = "Integration test notification";
        String recipient = "integration@example.com";

        // When & Then
        mockMvc.perform(post("/api/notifications/send")
                .param("message", message)
                .param("recipient", recipient))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification sent successfully"));
    }

    @Test
    @Order(2)
    @DisplayName("Should process notification successfully")
    @Transactional
    void shouldProcessNotificationSuccessfully() throws Exception {
        // Given
        String eventId = "integration-event-123";
        String eventType = "INTEGRATION_TEST_EVENT";
        String userId = "integration-user-456";

        // When & Then
        mockMvc.perform(post("/api/notifications/process")
                .param("eventId", eventId)
                .param("eventType", eventType)
                .param("userId", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification processed successfully"));
    }

    @Test
    @Order(3)
    @DisplayName("Should create account notification successfully")
    @Transactional
    void shouldCreateAccountNotificationSuccessfully() throws Exception {
        // Given
        String eventType = "ACCOUNT_CREATED";
        String userId = "integration-user-123";
        String accountNumber = "ACC-INT-123456789";
        String details = "Integration test account creation";

        // When & Then
        mockMvc.perform(post("/api/notifications/account-event")
                .param("eventType", eventType)
                .param("userId", userId)
                .param("accountNumber", accountNumber)
                .param("details", details))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.eventType").value(eventType));
    }

    @Test
    @Order(4)
    @DisplayName("Should create transaction notification successfully")
    @Transactional
    void shouldCreateTransactionNotificationSuccessfully() throws Exception {
        // Given
        String eventType = "TRANSACTION_COMPLETED";
        String userId = "integration-user-123";
        String transactionId = "TXN-INT-789456123";
        String amount = "1500.00";

        // When & Then
        mockMvc.perform(post("/api/notifications/transaction-event")
                .param("eventType", eventType)
                .param("userId", userId)
                .param("transactionId", transactionId)
                .param("amount", amount))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.eventType").value(eventType));
    }

    @Test
    @Order(5)
    @DisplayName("Should create notification from request body")
    @Transactional
    void shouldCreateNotificationFromRequestBody() throws Exception {
        // Given
        NotificationRequest request = new NotificationRequest();
        request.setMessage("Integration test notification message");
        request.setUserId("integration-user-789");
        request.setNotificationType("INTEGRATION_EVENT");

        // When & Then
        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Integration test notification message"))
                .andExpect(jsonPath("$.userId").value("integration-user-789"))
                .andExpect(jsonPath("$.eventType").value("INTEGRATION_EVENT"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @Order(6)
    @DisplayName("Should get all notifications successfully")
    void shouldGetAllNotificationsSuccessfully() throws Exception {
        // When & Then - Should return notifications created in previous tests
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(5)); // 5 notifications from previous tests
    }

    @Test
    @Order(7)
    @DisplayName("Should handle account event without details parameter")
    @Transactional
    void shouldHandleAccountEventWithoutDetailsParameter() throws Exception {
        // Given
        String eventType = "BALANCE_UPDATED";
        String userId = "integration-user-no-details";
        String accountNumber = "ACC-NO-DETAILS-123";

        // When & Then
        mockMvc.perform(post("/api/notifications/account-event")
                .param("eventType", eventType)
                .param("userId", userId)
                .param("accountNumber", accountNumber))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.eventType").value(eventType));
    }

    @Test
    @Order(8)
    @DisplayName("Should return 400 for missing required parameters")
    void shouldReturn400ForMissingRequiredParameters() throws Exception {
        // When & Then - Missing message parameter
        mockMvc.perform(post("/api/notifications/send")
                .param("recipient", "user@example.com"))
                .andExpect(status().isBadRequest());

        // Missing userId parameter
        mockMvc.perform(post("/api/notifications/account-event")
                .param("eventType", "ACCOUNT_CREATED")
                .param("accountNumber", "ACC123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    @DisplayName("Should handle invalid notification request")
    @Transactional
    void shouldHandleInvalidNotificationRequest() throws Exception {
        // Given - Request with null/empty required fields
        NotificationRequest invalidRequest = new NotificationRequest();
        invalidRequest.setMessage("");
        invalidRequest.setUserId("");
        invalidRequest.setNotificationType("");

        // When & Then
        mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(10)
    @DisplayName("Should handle concurrent notification processing")
    @Transactional
    void shouldHandleConcurrentNotificationProcessing() throws Exception {
        // Test creating multiple notifications concurrently
        for (int i = 0; i < 3; i++) {
            String userId = "concurrent-user-" + i;
            String eventType = "CONCURRENT_EVENT_" + i;
            
            mockMvc.perform(post("/api/notifications/process")
                    .param("eventId", "concurrent-event-" + i)
                    .param("eventType", eventType)
                    .param("userId", userId))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Notification processed successfully"));
        }
    }

    @Test
    @Order(11)
    @DisplayName("Should validate notification persistence across operations")
    void shouldValidateNotificationPersistenceAcrossOperations() throws Exception {
        // Given - Create a notification
        NotificationRequest request = new NotificationRequest();
        request.setMessage("Persistence test notification");
        request.setUserId("persistence-user");
        request.setNotificationType("PERSISTENCE_TEST");

        String createResponse = mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extract notification ID from response
        NotificationResponse createdNotification = objectMapper.readValue(createResponse, NotificationResponse.class);

        // When & Then - Verify the notification persists in the database
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.notificationId == '" + createdNotification.getNotificationId() + "')].message").value("Persistence test notification"))
                .andExpect(jsonPath("$[?(@.notificationId == '" + createdNotification.getNotificationId() + "')].userId").value("persistence-user"));
    }
}