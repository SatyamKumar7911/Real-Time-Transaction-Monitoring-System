package com.example.transaction_service.service;
import com.example.transaction_service.dto.*;
public interface TransactionService {
  BalanceResponse credit(CreditDebitRequest req);
  BalanceResponse debit(CreditDebitRequest req);
  BalanceResponse balance(String accountNumber);
  void transfer(TransferRequest req);
}