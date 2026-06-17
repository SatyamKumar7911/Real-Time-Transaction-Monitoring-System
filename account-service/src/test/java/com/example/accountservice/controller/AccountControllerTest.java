package com.example.accountservice.controller;

import com.example.accountservice.dto.*;
import com.example.accountservice.entity.UserEntity;
import com.example.accountservice.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AccountController
 * Tests all REST endpoints for user and account management
 */
@WebMvcTest(AccountController.class)
@DisplayName("Account Controller Tests")
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUser() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest("John Doe", "john@example.com", "1234567890");
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .phone("1234567890")
                .build();

        when(accountService.createUser(any(CreateUserRequest.class))).thenReturn(userEntity);

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.phone").value("1234567890"));
    }

    @Test
    @DisplayName("Should create account successfully")
    void shouldCreateAccount() throws Exception {
        // Given
        CreateAccountRequest request = new CreateAccountRequest(1L, "SAVINGS", "USD");
        AccountResponse response = new AccountResponse(1L, "ACC123", "SAVINGS", "USD", BigDecimal.ZERO, "ACTIVE", 1L);

        when(accountService.createAccount(any(CreateAccountRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountNumber").value("ACC123"))
                .andExpect(jsonPath("$.accountType").value("SAVINGS"))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Should get account by number")
    void shouldGetAccountByNumber() throws Exception {
        // Given
        String accountNumber = "ACC123";
        AccountResponse response = new AccountResponse(1L, accountNumber, "SAVINGS", "USD", BigDecimal.valueOf(1000), "ACTIVE", 1L);

        when(accountService.getAccountByNumber(eq(accountNumber))).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/v1/accounts/{accountNumber}", accountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(accountNumber))
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    @DisplayName("Should get account balance")
    void shouldGetAccountBalance() throws Exception {
        // Given
        String accountNumber = "ACC123";
        BalanceResponse response = new BalanceResponse(accountNumber, "USD", BigDecimal.valueOf(1500));

        when(accountService.getBalance(eq(accountNumber))).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/v1/accounts/{accountNumber}/balance", accountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(accountNumber))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.balance").value(1500));
    }

    @Test
    @DisplayName("Should get user accounts")
    void shouldGetUserAccounts() throws Exception {
        // Given
        Long userId = 1L;
        List<AccountResponse> accounts = Arrays.asList(
                new AccountResponse(1L, "ACC123", "SAVINGS", "USD", BigDecimal.valueOf(1000), "ACTIVE", userId),
                new AccountResponse(2L, "ACC456", "CHECKING", "EUR", BigDecimal.valueOf(500), "ACTIVE", userId)
        );

        when(accountService.getUserAccounts(eq(userId))).thenReturn(accounts);

        // When & Then
        mockMvc.perform(get("/api/v1/users/{userId}/accounts", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].accountNumber").value("ACC123"))
                .andExpect(jsonPath("$[1].accountNumber").value("ACC456"));
    }

    @Test
    @DisplayName("Should credit account successfully")
    void shouldCreditAccount() throws Exception {
        // Given
        CreditDebitRequest request = new CreditDebitRequest("ACC123", BigDecimal.valueOf(500), "DEPOSIT_REF");
        BalanceResponse response = new BalanceResponse("ACC123", "USD", BigDecimal.valueOf(1500));

        when(accountService.credit(any(CreditDebitRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/accounts/credit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC123"))
                .andExpect(jsonPath("$.balance").value(1500));
    }

    @Test
    @DisplayName("Should debit account successfully")
    void shouldDebitAccount() throws Exception {
        // Given
        CreditDebitRequest request = new CreditDebitRequest("ACC123", BigDecimal.valueOf(200), "WITHDRAWAL_REF");
        BalanceResponse response = new BalanceResponse("ACC123", "USD", BigDecimal.valueOf(800));

        when(accountService.debit(any(CreditDebitRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/accounts/debit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC123"))
                .andExpect(jsonPath("$.balance").value(800));
    }

    @Test
    @DisplayName("Should handle transfer request")
    void shouldHandleTransfer() throws Exception {
        // Given
        TransferRequest request = new TransferRequest("ACC123", "ACC456", BigDecimal.valueOf(300), "TRANSFER_REF");

        // When & Then
        mockMvc.perform(post("/api/v1/accounts/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return 400 for invalid user creation request")
    void shouldReturn400ForInvalidUserRequest() throws Exception {
        // Given - Invalid request with missing required fields
        CreateUserRequest invalidRequest = new CreateUserRequest("", "", "");

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for invalid account creation request")
    void shouldReturn400ForInvalidAccountRequest() throws Exception {
        // Given - Invalid request with null userId
        CreateAccountRequest invalidRequest = new CreateAccountRequest(null, "", "");

        // When & Then
        mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}