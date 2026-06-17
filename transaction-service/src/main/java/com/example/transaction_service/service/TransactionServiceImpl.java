package com.example.transaction_service.service;
import com.example.transaction_service.client.AccountHttpClient;
import com.example.transaction_service.client.NotificationHttpClient;
import com.example.transaction_service.dto.*;
import com.example.transaction_service.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
  private final AccountHttpClient accountClient;
  private final NotificationHttpClient notificationClient;
  private final TransactionEventPublisher eventPublisher;
  @Override
  public BalanceResponse credit(CreditDebitRequest req) {
    String transactionId = UUID.randomUUID().toString();
    
    // Publish transaction initiated event
    var initiatedEvent = new TransactionEvent("TRANSACTION_INITIATED", transactionId, 
        null, req.accountNumber(), req.amount(), "USD", LocalDateTime.now(), "PENDING", "Credit transaction");
    eventPublisher.publishTransactionEvent(initiatedEvent);
    
    try {
      var res = accountClient.credit(req);
      
      // Publish transaction completed event
      var completedEvent = new TransactionEvent("TRANSACTION_COMPLETED", transactionId, 
          null, req.accountNumber(), req.amount(), "USD", LocalDateTime.now(), "SUCCESS", "Credit completed");
      eventPublisher.publishTransactionEvent(completedEvent);
      
      //  notification for do not fail the transaction if notify fails
      try {
        notificationClient.notify(Map.of(
          "type", "CREDIT",
          "account", req.accountNumber(),
          "amount", req.amount(),
          "reference", req.reference()
        ));
      } catch (Exception ignored) {}
      return res;
    } catch (Exception e) {
      // Publish transaction failed event
      var failedEvent = new TransactionEvent("TRANSACTION_FAILED", transactionId, 
          null, req.accountNumber(), req.amount(), "USD", LocalDateTime.now(), "FAILED", e.getMessage());
      eventPublisher.publishTransactionEvent(failedEvent);
      throw e;
    }
  }
  @Override
  public BalanceResponse debit(CreditDebitRequest req) {
    String transactionId = UUID.randomUUID().toString();
    
    // Publish transaction initiated event
    var initiatedEvent = new TransactionEvent("TRANSACTION_INITIATED", transactionId, 
        req.accountNumber(), null, req.amount(), "USD", LocalDateTime.now(), "PENDING", "Debit transaction");
    eventPublisher.publishTransactionEvent(initiatedEvent);
    
    try {
      var res = accountClient.debit(req);
      
      // Publish transaction completed event
      var completedEvent = new TransactionEvent("TRANSACTION_COMPLETED", transactionId, 
          req.accountNumber(), null, req.amount(), "USD", LocalDateTime.now(), "SUCCESS", "Debit completed");
      eventPublisher.publishTransactionEvent(completedEvent);
      
      try {
        notificationClient.notify(Map.of(
          "type", "DEBIT",
          "account", req.accountNumber(),
          "amount", req.amount(),
          "reference", req.reference()
        ));
      } catch (Exception ignored) {}
      return res;
    } catch (Exception e) {
      // Publish transaction failed event
      var failedEvent = new TransactionEvent("TRANSACTION_FAILED", transactionId, 
          req.accountNumber(), null, req.amount(), "USD", LocalDateTime.now(), "FAILED", e.getMessage());
      eventPublisher.publishTransactionEvent(failedEvent);
      throw e;
    }
  }
  @Override
  public BalanceResponse balance(String accountNumber) {
    return accountClient.balance(accountNumber);
  }
  @Override
  public void transfer(TransferRequest req) {
    String transactionId = UUID.randomUUID().toString();
    
    // Publish transaction initiated event
    var initiatedEvent = new TransactionEvent("TRANSACTION_INITIATED", transactionId, 
        req.fromAccount(), req.toAccount(), req.amount(), "USD", LocalDateTime.now(), "PENDING", "Transfer transaction");
    eventPublisher.publishTransactionEvent(initiatedEvent);
    
    try {
      accountClient.transfer(req);
      
      // Publish transaction completed event
      var completedEvent = new TransactionEvent("TRANSACTION_COMPLETED", transactionId, 
          req.fromAccount(), req.toAccount(), req.amount(), "USD", LocalDateTime.now(), "SUCCESS", "Transfer completed");
      eventPublisher.publishTransactionEvent(completedEvent);
      
      try {
        notificationClient.notify(Map.of(
          "type", "TRANSFER",
          "from", req.fromAccount(),
          "to", req.toAccount(),
          "amount", req.amount(),
          "reference", req.reference()
        ));
      } catch (Exception ignored) {}
    } catch (Exception e) {
      // Publish transaction failed event
      var failedEvent = new TransactionEvent("TRANSACTION_FAILED", transactionId, 
          req.fromAccount(), req.toAccount(), req.amount(), "USD", LocalDateTime.now(), "FAILED", e.getMessage());
      eventPublisher.publishTransactionEvent(failedEvent);
      throw e;
    }
  }
}