package com.example.transaction_service.controller;

import com.example.transaction_service.dto.*;
import com.example.transaction_service.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for TransactionController
 * Tests all REST endpoints for transaction operations
 */
@WebMvcTest(controllers = TransactionController.class)
@ContextConfiguration(classes = {TransactionController.class, TransactionControllerTest.TestConfig.class})
@TestPropertySource(properties = {
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
        "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
        "org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration",
    "security.jwt.enabled=false",
    "spring.web.resources.add-mappings=false"
})
@DisplayName("Transaction Controller Tests")
class TransactionControllerTest {

    @Configuration
    static class TestConfig {
        @Bean
        public LocalValidatorFactoryBean validator() {
            return new LocalValidatorFactoryBean();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should credit account successfully")
    void shouldCreditAccount() throws Exception {
        // Given
        String accountNumber = "ACC123456789";
        CreditDebitRequest request = new CreditDebitRequest(accountNumber, BigDecimal.valueOf(500), "CREDIT_REF");
        BalanceResponse response = new BalanceResponse(accountNumber, "USD", BigDecimal.valueOf(1500));

        when(transactionService.credit(any(CreditDebitRequest.class))).thenReturn(response);

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
    @DisplayName("Should debit account successfully")
    void shouldDebitAccount() throws Exception {
        // Given
        String accountNumber = "ACC123456789";
        CreditDebitRequest request = new CreditDebitRequest(accountNumber, BigDecimal.valueOf(200), "DEBIT_REF");
        BalanceResponse response = new BalanceResponse(accountNumber, "USD", BigDecimal.valueOf(800));

        when(transactionService.debit(any(CreditDebitRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/transactions/debit/{accountNumber}", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(accountNumber))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.balance").value(800));
    }

    @Test
    @DisplayName("Should get account balance")
    void shouldGetAccountBalance() throws Exception {
        // Given
        String accountNumber = "ACC123456789";
        BalanceResponse response = new BalanceResponse(accountNumber, "USD", BigDecimal.valueOf(1200));

        when(transactionService.balance(eq(accountNumber))).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/v1/transactions/{accountNumber}/balance", accountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(accountNumber))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.balance").value(1200));
    }

    @Test
    @DisplayName("Should handle transfer successfully")
    void shouldHandleTransfer() throws Exception {
        // Given
        TransferRequest request = new TransferRequest(
                "ACC123456789",
                "ACC987654321",
                BigDecimal.valueOf(300),
                "TRANSFER_REF"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 400 for invalid credit request")
    void shouldReturn400ForInvalidCreditRequest() throws Exception {
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
    @DisplayName("Should return 400 for invalid debit request")
    void shouldReturn400ForInvalidDebitRequest() throws Exception {
        // Given
        String accountNumber = "ACC123456789";
        CreditDebitRequest invalidRequest = new CreditDebitRequest("", BigDecimal.valueOf(-100), "");

        // When & Then
        mockMvc.perform(post("/api/v1/transactions/debit/{accountNumber}", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for invalid transfer request")
    void shouldReturn400ForInvalidTransferRequest() throws Exception {
        // Given
        TransferRequest invalidRequest = new TransferRequest("", "", null, "");

        // When & Then
        mockMvc.perform(post("/api/v1/transactions/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}