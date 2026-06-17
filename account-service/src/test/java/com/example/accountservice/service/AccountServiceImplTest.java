package com.example.accountservice.service;

import com.example.accountservice.dto.*;
import com.example.accountservice.entity.*;
import com.example.accountservice.service.AccountEventPublisher;
import com.example.accountservice.exception.*;
import com.example.accountservice.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.example.accountservice.entity.LogEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AccountServiceImpl
 * Tests business logic and service layer functionality
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Account Service Tests")
class AccountServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository accountRepository;

    @Mock
    private LogRepository logRepository;

    @Mock
    private AccountEventPublisher eventPublisher;

    @InjectMocks
    private AccountServiceImpl accountService;

    private UserEntity testUser;
    private BankAccountEntity testAccount;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .phone("1234567890")
                .build();

        testAccount = BankAccountEntity.builder()
                .id(1L)
                .accountNumber("ACC123456789")
                .user(testUser)
                .accountType("SAVINGS")
                .currency("USD")
                .balance(BigDecimal.valueOf(1000))
                .status("ACTIVE")
                .version(0L)
                .build();
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        // Given
        CreateUserRequest request = new CreateUserRequest("Jane Doe", "jane@example.com", "9876543210");
        UserEntity savedUser = UserEntity.builder()
                .id(2L)
                .name("Jane Doe")
                .email("jane@example.com")
                .phone("9876543210")
                .build();

        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        // When
        UserEntity result = accountService.createUser(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("Jane Doe");
        assertThat(result.getEmail()).isEqualTo("jane@example.com");
        assertThat(result.getPhone()).isEqualTo("9876543210");

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should create account successfully")
    void shouldCreateAccountSuccessfully() {
        // Given
        CreateAccountRequest request = new CreateAccountRequest(1L, "CHECKING", "EUR");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(accountRepository.save(any(BankAccountEntity.class))).thenReturn(testAccount);

        // When
        AccountResponse result = accountService.createAccount(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.accountNumber()).isEqualTo("ACC123456789");
        assertThat(result.userId()).isEqualTo(1L);
        // Note: AccountResponse doesn't have userName field, only userId

        verify(userRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(any(BankAccountEntity.class));
        verify(logRepository, times(1)).save(any(LogEntity.class));
        verify(eventPublisher, times(1)).publishAccountEvent(any());
    }

    @Test
    @DisplayName("Should throw exception when creating account for non-existent user")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        CreateAccountRequest request = new CreateAccountRequest(999L, "SAVINGS", "USD");
        
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> accountService.createAccount(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found: 999");

        verify(userRepository, times(1)).findById(999L);
        verifyNoInteractions(accountRepository, logRepository, eventPublisher);
    }

    @Test
    @DisplayName("Should get account by number successfully")
    void shouldGetAccountByNumberSuccessfully() {
        // Given
        String accountNumber = "ACC123456789";
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(testAccount));

        // When
        AccountResponse result = accountService.getAccountByNumber(accountNumber);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.accountNumber()).isEqualTo(accountNumber);
        assertThat(result.balance()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(result.currency()).isEqualTo("USD");

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
    }

    @Test
    @DisplayName("Should throw exception when account not found")
    void shouldThrowExceptionWhenAccountNotFound() {
        // Given
        String accountNumber = "INVALID_ACC";
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> accountService.getAccountByNumber(accountNumber))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Account not found: INVALID_ACC");

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
    }

    @Test
    @DisplayName("Should get balance successfully")
    void shouldGetBalanceSuccessfully() {
        // Given
        String accountNumber = "ACC123456789";
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(testAccount));

        // When
        BalanceResponse result = accountService.getBalance(accountNumber);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.accountNumber()).isEqualTo(accountNumber);
        assertThat(result.balance()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(result.currency()).isEqualTo("USD");

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
    }

    @Test
    @DisplayName("Should get user accounts successfully")
    void shouldGetUserAccountsSuccessfully() {
        // Given
        Long userId = 1L;
        BankAccountEntity account2 = BankAccountEntity.builder()
                .id(2L)
                .accountNumber("ACC987654321")
                .user(testUser)
                .accountType("CHECKING")
                .currency("EUR")
                .balance(BigDecimal.valueOf(500))
                .status("ACTIVE")
                .version(0L)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(accountRepository.findByUserId(userId)).thenReturn(Arrays.asList(testAccount, account2));

        // When
        List<AccountResponse> result = accountService.getUserAccounts(userId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).accountNumber()).isEqualTo("ACC123456789");
        assertThat(result.get(1).accountNumber()).isEqualTo("ACC987654321");

        verify(userRepository, times(1)).findById(userId);
        verify(accountRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Should credit account successfully")
    void shouldCreditAccountSuccessfully() {
        // Given
        CreditDebitRequest request = new CreditDebitRequest("ACC123456789", BigDecimal.valueOf(500), "DEPOSIT_REF");
        BankAccountEntity updatedAccount = BankAccountEntity.builder()
                .id(1L)
                .accountNumber("ACC123456789")
                .user(testUser)
                .accountType("SAVINGS")
                .currency("USD")
                .balance(BigDecimal.valueOf(1500))
                .status("ACTIVE")
                .version(1L)
                .build();

        when(accountRepository.findByAccountNumber("ACC123456789")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(BankAccountEntity.class))).thenReturn(updatedAccount);

        // When
        BalanceResponse result = accountService.credit(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.accountNumber()).isEqualTo("ACC123456789");
        assertThat(result.balance()).isEqualTo(BigDecimal.valueOf(1500));
        assertThat(result.currency()).isEqualTo("USD");

        verify(accountRepository, times(1)).findByAccountNumber("ACC123456789");
        verify(accountRepository, times(1)).save(any(BankAccountEntity.class));
        verify(logRepository, times(1)).save(any(LogEntity.class));
        verify(eventPublisher, times(1)).publishAccountEvent(any());
    }

    @Test
    @DisplayName("Should debit account successfully")
    void shouldDebitAccountSuccessfully() {
        // Given
        CreditDebitRequest request = new CreditDebitRequest("ACC123456789", BigDecimal.valueOf(200), "WITHDRAWAL_REF");
        BankAccountEntity updatedAccount = BankAccountEntity.builder()
                .id(1L)
                .accountNumber("ACC123456789")
                .user(testUser)
                .accountType("SAVINGS")
                .currency("USD")
                .balance(BigDecimal.valueOf(800))
                .status("ACTIVE")
                .version(1L)
                .build();

        when(accountRepository.findByAccountNumber("ACC123456789")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(BankAccountEntity.class))).thenReturn(updatedAccount);

        // When
        BalanceResponse result = accountService.debit(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.accountNumber()).isEqualTo("ACC123456789");
        assertThat(result.balance()).isEqualTo(BigDecimal.valueOf(800));
        assertThat(result.currency()).isEqualTo("USD");

        verify(accountRepository, times(1)).findByAccountNumber("ACC123456789");
        verify(accountRepository, times(1)).save(any(BankAccountEntity.class));
        verify(logRepository, times(1)).save(any(LogEntity.class));
        verify(eventPublisher, times(1)).publishAccountEvent(any());
    }

    @Test
    @DisplayName("Should throw exception when debiting with insufficient funds")
    void shouldThrowExceptionWhenInsufficientFunds() {
        // Given
        CreditDebitRequest request = new CreditDebitRequest("ACC123456789", BigDecimal.valueOf(2000), "WITHDRAWAL_REF");
        
        when(accountRepository.findByAccountNumber("ACC123456789")).thenReturn(Optional.of(testAccount));

        // When & Then
        assertThatThrownBy(() -> accountService.debit(request))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessage("Insufficient balance");

        verify(accountRepository, times(1)).findByAccountNumber("ACC123456789");
        verify(accountRepository, never()).save(any());
        verifyNoInteractions(logRepository, eventPublisher);
    }

    @Test
    @DisplayName("Should throw exception when crediting non-existent account")
    void shouldThrowExceptionWhenCreditingNonExistentAccount() {
        // Given
        CreditDebitRequest request = new CreditDebitRequest("INVALID_ACC", BigDecimal.valueOf(100), "DEPOSIT_REF");
        
        when(accountRepository.findByAccountNumber("INVALID_ACC")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> accountService.credit(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Account not found: INVALID_ACC");

        verify(accountRepository, times(1)).findByAccountNumber("INVALID_ACC");
        verifyNoInteractions(logRepository, eventPublisher);
    }
}