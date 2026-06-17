package com.example.accountservice.integration;

import com.example.accountservice.AccountServiceApplication;
import com.example.accountservice.dto.*;
import com.example.accountservice.entity.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.assertThat;
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
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Account Service
 * Tests complete flow with real database and Kafka
 */
@SpringBootTest(
        classes = AccountServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureWebMvc
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Account Service Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled("Docker environment required - skip for unit testing")
class AccountServiceIntegrationTest {

    @Container
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Database configuration
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        
        // JPA configuration
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "true");
        
        // Kafka configuration
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.consumer.group-id", () -> "test-group");
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
        
        // Eureka configuration (disable for tests)
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.discovery.enabled", () -> "false");
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    // Test data
    private static Long testUserId;
    private static String testAccountNumber;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Order(1)
    @DisplayName("Should create user and return user details")
    void shouldCreateUser() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest(
                "Integration Test User",
                "integration@example.com",
                "1234567890"
        );

        // When & Then
        String response = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Test User"))
                .andExpect(jsonPath("$.email").value("integration@example.com"))
                .andExpect(jsonPath("$.phone").value("1234567890"))
                .andReturn().getResponse().getContentAsString();

        // Extract user ID for subsequent tests
        UserEntity user = objectMapper.readValue(response, UserEntity.class);
        testUserId = user.getId();
        
        assertThat(testUserId).isNotNull();
    }

    @Test
    @Order(2)
    @DisplayName("Should create account for existing user")
    void shouldCreateAccount() throws Exception {
        // Given
        assertThat(testUserId).isNotNull();
        
        CreateAccountRequest request = new CreateAccountRequest(
                testUserId,
                "SAVINGS",
                "USD"
        );

        // When & Then
        String response = mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountType").value("SAVINGS"))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.userId").value(testUserId))
                .andReturn().getResponse().getContentAsString();

        // Extract account number for subsequent tests
        AccountResponse accountResponse = objectMapper.readValue(response, AccountResponse.class);
        testAccountNumber = accountResponse.accountNumber();
        
        assertThat(testAccountNumber).isNotNull();
    }

    @Test
    @Order(3)
    @DisplayName("Should retrieve account by number")
    void shouldGetAccountByNumber() throws Exception {
        // Given
        assertThat(testAccountNumber).isNotNull();

        // When & Then
        mockMvc.perform(get("/api/v1/accounts/{accountNumber}", testAccountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(testAccountNumber))
                .andExpect(jsonPath("$.accountType").value("SAVINGS"))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    @Order(4)
    @DisplayName("Should get account balance")
    void shouldGetAccountBalance() throws Exception {
        // Given
        assertThat(testAccountNumber).isNotNull();

        // When & Then
        mockMvc.perform(get("/api/v1/accounts/{accountNumber}/balance", testAccountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(testAccountNumber))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    @Order(5)
    @DisplayName("Should credit account and update balance")
    @Transactional
    void shouldCreditAccount() throws Exception {
        // Given
        assertThat(testAccountNumber).isNotNull();
        
        CreditDebitRequest request = new CreditDebitRequest(
                testAccountNumber,
                BigDecimal.valueOf(1000),
                "INTEGRATION_TEST_DEPOSIT"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/accounts/credit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(testAccountNumber))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    @Order(6)
    @DisplayName("Should debit account and update balance")
    @Transactional
    void shouldDebitAccount() throws Exception {
        // Given
        assertThat(testAccountNumber).isNotNull();
        
        CreditDebitRequest request = new CreditDebitRequest(
                testAccountNumber,
                BigDecimal.valueOf(300),
                "INTEGRATION_TEST_WITHDRAWAL"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/accounts/debit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(testAccountNumber))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.balance").value(700));
    }

    @Test
    @Order(7)
    @DisplayName("Should get user accounts")
    void shouldGetUserAccounts() throws Exception {
        // Given
        assertThat(testUserId).isNotNull();

        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}/accounts", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].accountNumber").value(testAccountNumber))
                .andExpect(jsonPath("$[0].userId").value(testUserId));
    }

    @Test
    @Order(8)
    @DisplayName("Should return 404 for non-existent account")
    void shouldReturn404ForNonExistentAccount() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/accounts/NONEXISTENT"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    @DisplayName("Should return 400 for insufficient funds")
    void shouldReturn400ForInsufficientFunds() throws Exception {
        // Given
        assertThat(testAccountNumber).isNotNull();
        
        CreditDebitRequest request = new CreditDebitRequest(
                testAccountNumber,
                BigDecimal.valueOf(10000), // More than available balance
                "INSUFFICIENT_FUNDS_TEST"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/accounts/debit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(10)
    @DisplayName("Should handle transfer operation")
    @Transactional
    void shouldHandleTransfer() throws Exception {
        // Given - Create a second account for transfer
        CreateAccountRequest accountRequest = new CreateAccountRequest(
                testUserId,
                "CHECKING",
                "USD"
        );

        String response = mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        AccountResponse secondAccount = objectMapper.readValue(response, AccountResponse.class);
        
        // Create transfer request
        TransferRequest transferRequest = new TransferRequest(
                testAccountNumber,
                secondAccount.accountNumber(),
                BigDecimal.valueOf(200),
                "INTEGRATION_TEST_TRANSFER"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk());
    }
}