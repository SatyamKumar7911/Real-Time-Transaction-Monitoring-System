package com.example.transaction_service.service;

import com.example.transaction_service.client.AccountHttpClient;
import com.example.transaction_service.dto.*;
import com.example.transaction_service.service.TransactionEventPublisher;
import com.example.transaction_service.event.TransactionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TransactionServiceImpl
 * Tests business logic and service layer functionality
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Transaction Service Tests")
class TransactionServiceImplTest {

    @Mock
    private AccountHttpClient accountClient;

    @Mock
    private TransactionEventPublisher eventPublisher;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private BalanceResponse mockBalanceResponse;
    private CreditDebitRequest creditRequest;
    private CreditDebitRequest debitRequest;
    private TransferRequest transferRequest;

    @BeforeEach
    void setUp() {
        mockBalanceResponse = new BalanceResponse("ACC123456789", "USD", BigDecimal.valueOf(1000));
        
        creditRequest = new CreditDebitRequest(
                "ACC123456789",
                BigDecimal.valueOf(500),
                "CREDIT_REF_123"
        );
        
        debitRequest = new CreditDebitRequest(
                "ACC123456789",
                BigDecimal.valueOf(200),
                "DEBIT_REF_123"
        );
        
        transferRequest = new TransferRequest(
                "ACC123456789",
                "ACC987654321",
                BigDecimal.valueOf(300),
                "TRANSFER_REF_123"
        );
    }

    @Test
    @DisplayName("Should credit account successfully")
    void shouldCreditAccountSuccessfully() {
        // Given
        BalanceResponse expectedResponse = new BalanceResponse("ACC123456789", "USD", BigDecimal.valueOf(1500));
        when(accountClient.credit(any(CreditDebitRequest.class))).thenReturn(expectedResponse);

        // When
        BalanceResponse result = transactionService.credit(creditRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.accountNumber()).isEqualTo("ACC123456789");
        assertThat(result.currency()).isEqualTo("USD");
        assertThat(result.balance()).isEqualTo(BigDecimal.valueOf(1500));

        verify(accountClient, times(1)).credit(creditRequest);
        verify(eventPublisher, times(2)).publishTransactionEvent(any(TransactionEvent.class));
    }

    @Test
    @DisplayName("Should debit account successfully")
    void shouldDebitAccountSuccessfully() {
        // Given
        BalanceResponse expectedResponse = new BalanceResponse("ACC123456789", "USD", BigDecimal.valueOf(800));
        when(accountClient.debit(any(CreditDebitRequest.class))).thenReturn(expectedResponse);

        // When
        BalanceResponse result = transactionService.debit(debitRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.accountNumber()).isEqualTo("ACC123456789");
        assertThat(result.currency()).isEqualTo("USD");
        assertThat(result.balance()).isEqualTo(BigDecimal.valueOf(800));

        verify(accountClient, times(1)).debit(debitRequest);
        verify(eventPublisher, times(2)).publishTransactionEvent(any(TransactionEvent.class));
    }

    @Test
    @DisplayName("Should get account balance successfully")
    void shouldGetAccountBalanceSuccessfully() {
        // Given
        String accountNumber = "ACC123456789";
        when(accountClient.balance(eq(accountNumber))).thenReturn(mockBalanceResponse);

        // When
        BalanceResponse result = transactionService.balance(accountNumber);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.accountNumber()).isEqualTo("ACC123456789");
        assertThat(result.currency()).isEqualTo("USD");
        assertThat(result.balance()).isEqualTo(BigDecimal.valueOf(1000));

        verify(accountClient, times(1)).balance(accountNumber);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("Should handle transfer successfully")
    void shouldHandleTransferSuccessfully() {
        // Given - No exceptions thrown means successful transfer
        
        // When
        transactionService.transfer(transferRequest);

        // Then
        verify(accountClient, times(1)).transfer(transferRequest);
        verify(eventPublisher, times(2)).publishTransactionEvent(any(TransactionEvent.class));
    }

    @Test
    @DisplayName("Should publish failed event when credit fails")
    void shouldPublishFailedEventWhenCreditFails() {
        // Given
        when(accountClient.credit(any(CreditDebitRequest.class)))
                .thenThrow(new RuntimeException("Account service error"));

        // When & Then
        assertThatThrownBy(() -> transactionService.credit(creditRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Account service error");

        verify(accountClient, times(1)).credit(creditRequest);
        verify(eventPublisher, times(2)).publishTransactionEvent(any(TransactionEvent.class));
    }

    @Test
    @DisplayName("Should publish failed event when debit fails")
    void shouldPublishFailedEventWhenDebitFails() {
        // Given
        when(accountClient.debit(any(CreditDebitRequest.class)))
                .thenThrow(new RuntimeException("Insufficient funds"));

        // When & Then
        assertThatThrownBy(() -> transactionService.debit(debitRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Insufficient funds");

        verify(accountClient, times(1)).debit(debitRequest);
        verify(eventPublisher, times(2)).publishTransactionEvent(any(TransactionEvent.class));
    }

    @Test
    @DisplayName("Should publish failed event when transfer fails")
    void shouldPublishFailedEventWhenTransferFails() {
        // Given
        doThrow(new RuntimeException("Transfer failed")).when(accountClient).transfer(any());

        // When & Then
        assertThatThrownBy(() -> transactionService.transfer(transferRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Transfer failed");

        verify(accountClient, times(1)).transfer(transferRequest);
        verify(eventPublisher, times(2)).publishTransactionEvent(any(TransactionEvent.class));
    }

    @Test
    @DisplayName("Should handle balance query failure gracefully")
    void shouldHandleBalanceQueryFailureGracefully() {
        // Given
        String accountNumber = "ACC123456789";
        when(accountClient.balance(eq(accountNumber)))
                .thenThrow(new RuntimeException("Account not found"));

        // When & Then
        assertThatThrownBy(() -> transactionService.balance(accountNumber))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Account not found");

        verify(accountClient, times(1)).balance(accountNumber);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("Should handle null credit request")
    void shouldHandleNullCreditRequest() {
        // When & Then
        assertThatThrownBy(() -> transactionService.credit(null))
                .isInstanceOf(RuntimeException.class);

        verifyNoInteractions(accountClient, eventPublisher);
    }

    @Test
    @DisplayName("Should handle null debit request")
    void shouldHandleNullDebitRequest() {
        // When & Then
        assertThatThrownBy(() -> transactionService.debit(null))
                .isInstanceOf(RuntimeException.class);

        verifyNoInteractions(accountClient, eventPublisher);
    }

    @Test
    @DisplayName("Should handle null transfer request")
    void shouldHandleNullTransferRequest() {
        // When & Then
        assertThatThrownBy(() -> transactionService.transfer(null))
                .isInstanceOf(RuntimeException.class);

        verifyNoInteractions(accountClient, eventPublisher);
    }
}
