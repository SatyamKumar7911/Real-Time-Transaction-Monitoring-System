package com.example.accountservice.service;
import com.example.accountservice.dto.*;
import com.example.accountservice.entity.UserEntity;
import java.util.List;
public interface AccountService {
UserEntity createUser(CreateUserRequest req);
List<UserEntity> getAllUsers();
UserEntity getUserById(Long id);
UserEntity updateUser(Long id, CreateUserRequest req);
void deleteUser(Long id);
AccountResponse createAccount(CreateAccountRequest req);
AccountResponse getAccountByNumber(String accountNumber);
AccountResponse getAccountById(Long id);
BalanceResponse getBalance(String accountNumber);
List<AccountResponse> getUserAccounts(Long userId);
BalanceResponse credit(CreditDebitRequest req);
BalanceResponse debit(CreditDebitRequest req);
AccountResponse deposit(Long accountId, DepositWithdrawRequest req);
AccountResponse withdraw(Long accountId, DepositWithdrawRequest req);
void transfer(TransferRequest req);
}
