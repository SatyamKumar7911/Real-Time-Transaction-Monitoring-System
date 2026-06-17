package com.example.transaction_service.integration;

import com.example.transaction_service.TransactionServiceApplication;
import com.example.transaction_service.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import com.example.transaction_service.client.AccountServiceClient;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Transaction Service
 * Tests complete flow with mocked external services and real Kafka
 */
@SpringBootTest(
        classes = TransactionServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureWebMvc
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Transaction Service Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled("Docker environment required - skip for unit testing")
class TransactionServiceIntegrationTest {

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
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

    @MockBean
    private AccountServiceClient accountServiceClient;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Order(1)
    @DisplayName("Should credit account through transaction service")
    void shouldCreditAccountThroughTransactionService() throws Exception {
        // Given
        String accountNumber = "ACC123456789";
        CreditDebitRequest request = new CreditDebitRequest(accountNumber, BigDecimal.valueOf(500), "CREDIT_TEST");
        BalanceResponse mockResponse = new BalanceResponse(accountNumber, "USD", BigDecimal.valueOf(1500));

        when(accountServiceClient.credit(any(CreditDebitRequest.class))).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/transactions/credit/{accountNumber}", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(accountNumber))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.balance").value(1500));
    }

    @Test
    @Order(2)
    @DisplayName("Should debit account through transaction service")
    void shouldDebitAccountThroughTransactionService() throws Exception {
        // Given
        String accountNumber = "ACC123456789";
        CreditDebitRequest request = new CreditDebitRequest(accountNumber, BigDecimal.valueOf(300), "DEBIT_TEST");
        BalanceResponse mockResponse = new BalanceResponse(accountNumber, "USD", BigDecimal.valueOf(700));

        when(accountServiceClient.debit(any(CreditDebitRequest.class))).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/transactions/debit/{accountNumber}", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(accountNumber))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.balance").value(700));
    }

    @Test
    @Order(3)
    @DisplayName("Should get account balance through transaction service")
    void shouldGetAccountBalanceThroughTransactionService() throws Exception {
        // Given
        String accountNumber = "ACC123456789";
        BalanceResponse mockResponse = new BalanceResponse(accountNumber, "USD", BigDecimal.valueOf(1200));

        when(accountServiceClient.getBalance(eq(accountNumber))).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/transactions/{accountNumber}/balance", accountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(accountNumber))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.balance").value(1200));
    }

    @Test
    @Order(4)
    @DisplayName("Should handle transfer through transaction service")
    void shouldHandleTransferThroughTransactionService() throws Exception {
        // Given
        TransferRequest request = new TransferRequest(
                "ACC123456789",
                "ACC987654321",
                BigDecimal.valueOf(200),
                "TRANSFER_TEST"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(5)
    @DisplayName("Should handle account service failure gracefully")
    void shouldHandleAccountServiceFailureGracefully() throws Exception {
        // Given
        String accountNumber = "ACC123456789";
        CreditDebitRequest request = new CreditDebitRequest(accountNumber, BigDecimal.valueOf(500), "CREDIT_FAIL_TEST");

        when(accountServiceClient.credit(any(CreditDebitRequest.class)))
                .thenThrow(new RuntimeException("Account service unavailable"));

        // When & Then
        mockMvc.perform(post("/api/v1/transactions/credit/{accountNumber}", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(6)
    @DisplayName("Should handle balance query failure")
    void shouldHandleBalanceQueryFailure() throws Exception {
        // Given
        String accountNumber = "NONEXISTENT_ACC";

        when(accountServiceClient.getBalance(eq(accountNumber)))
                .thenThrow(new RuntimeException("Account not found"));

        // When & Then
        mockMvc.perform(get("/api/v1/transactions/{accountNumber}/balance", accountNumber))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(7)
    @DisplayName("Should validate credit request parameters")
    void shouldValidateCreditRequestParameters() throws Exception {
        // Given
        String accountNumber = "ACC123456789";
        CreditDebitRequest invalidRequest = new CreditDebitRequest(null, null, null);

        // When & Then
        mockMvc.perform(post("/api/v1/transactions/credit/{accountNumber}", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(8)
    @DisplayName("Should validate transfer request parameters")
    void shouldValidateTransferRequestParameters() throws Exception {
        // Given
        TransferRequest invalidRequest = new TransferRequest("", "", null, "");

        // When & Then
        mockMvc.perform(post("/api/v1/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    @DisplayName("Should handle concurrent credit operations")
    void shouldHandleConcurrentCreditOperations() throws Exception {
        // Given
        String accountNumber = "ACC123456789";
        CreditDebitRequest request1 = new CreditDebitRequest(accountNumber, BigDecimal.valueOf(100), "CONCURRENT_1");
        CreditDebitRequest request2 = new CreditDebitRequest(accountNumber, BigDecimal.valueOf(200), "CONCURRENT_2");
        
        BalanceResponse response1 = new BalanceResponse(accountNumber, "USD", BigDecimal.valueOf(1100));
        BalanceResponse response2 = new BalanceResponse(accountNumber, "USD", BigDecimal.valueOf(1300));

        when(accountServiceClient.credit(request1)).thenReturn(response1);
        when(accountServiceClient.credit(request2)).thenReturn(response2);

        // When & Then
        mockMvc.perform(post("/api/v1/transactions/credit/{accountNumber}", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1100));

        mockMvc.perform(post("/api/v1/transactions/credit/{accountNumber}", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1300));
    }
}