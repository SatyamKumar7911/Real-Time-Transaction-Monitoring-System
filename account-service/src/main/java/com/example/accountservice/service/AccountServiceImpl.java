package com.example.accountservice.service;
import com.example.accountservice.dto.*;
import com.example.accountservice.entity.*;
import com.example.accountservice.event.AccountEvent;
import com.example.accountservice.exception.*;
import com.example.accountservice.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
private final UserRepository userRepo;
private final BankAccountRepository accRepo;
private final LogRepository logRepo;
private final AccountEventPublisher eventPublisher;
@Override
public UserEntity createUser(CreateUserRequest req) {
 var u = UserEntity.builder().name(req.name()).email(req.email()).phone(req.phone()).build();
 return userRepo.save(u);
}

@Override
public List<UserEntity> getAllUsers() {
 return userRepo.findAll();
}

@Override
public UserEntity getUserById(Long id) {
 return userRepo.findById(id)
   .orElseThrow(() -> new NotFoundException("User not found: " + id));
}

@Override
@Transactional
public UserEntity updateUser(Long id, CreateUserRequest req) {
 var user = userRepo.findById(id)
   .orElseThrow(() -> new NotFoundException("User not found: " + id));
 user.setName(req.name());
 user.setEmail(req.email());
 user.setPhone(req.phone());
 return userRepo.save(user);
}

@Override
@Transactional
public void deleteUser(Long id) {
 if (!userRepo.existsById(id)) {
   throw new NotFoundException("User not found: " + id);
 }
 userRepo.deleteById(id);
}

@Override
public AccountResponse createAccount(CreateAccountRequest req) {
 var user = userRepo.findById(req.userId())
   .orElseThrow(() -> new NotFoundException("User not found: " + req.userId()));
 var acc = BankAccountEntity.builder()
   .user(user)
   .accountNumber(genAccNo())
   .accountType(req.accountType())
   .currency(req.currency())
   .balance(BigDecimal.ZERO)
   .status("ACTIVE")
   .version(0L)
   .build();
 var saved = accRepo.save(acc);
 log(saved, "CREATE_ACCOUNT", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "create");
 
 // Publish account created event
 var event = new AccountEvent("ACCOUNT_CREATED", saved.getAccountNumber(), saved.getBalance(),
     saved.getCurrency(), saved.getUser().getId(), LocalDateTime.now(), null);
 eventPublisher.publishAccountEvent(event);
 
 return map(saved);
}
@Override
public AccountResponse getAccountByNumber(String accountNumber) {
 return accRepo.findByAccountNumber(accountNumber).map(this::map)
   .orElseThrow(() -> new NotFoundException("Account not found: " + accountNumber));
}

@Override
public AccountResponse getAccountById(Long id) {
 return accRepo.findById(id).map(this::map)
   .orElseThrow(() -> new NotFoundException("Account not found with ID: " + id));
}

@Override
public BalanceResponse getBalance(String accountNumber) {
 var acc = accRepo.findByAccountNumber(accountNumber)
   .orElseThrow(() -> new NotFoundException("Account not found: " + accountNumber));
 return new BalanceResponse(acc.getAccountNumber(), acc.getCurrency(), acc.getBalance());
}
@Override
public List<AccountResponse> getUserAccounts(Long userId) {
 userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User not found: " + userId));
 return accRepo.findByUserId(userId).stream().map(this::map).toList();
}
@Override @Transactional
public BalanceResponse credit(CreditDebitRequest req) {
 var acc = accRepo.findByAccountNumber(req.accountNumber())
   .orElseThrow(() -> new NotFoundException("Account not found: " + req.accountNumber()));
 var prev = acc.getBalance();
 var next = prev.add(req.amount());
 acc.setBalance(next);
 var saved = accRepo.save(acc);
 log(saved, "CREDIT", req.amount(), prev, next, idOrNew(req.reference()));
 
 // Publish balance updated event
 var event = new AccountEvent("BALANCE_UPDATED", saved.getAccountNumber(), saved.getBalance(),
     saved.getCurrency(), saved.getUser().getId(), LocalDateTime.now(), idOrNew(req.reference()));
 eventPublisher.publishAccountEvent(event);
 
 return new BalanceResponse(saved.getAccountNumber(), saved.getCurrency(), saved.getBalance());
}
@Override @Transactional
public BalanceResponse debit(CreditDebitRequest req) {
 var acc = accRepo.findByAccountNumber(req.accountNumber())
   .orElseThrow(() -> new NotFoundException("Account not found: " + req.accountNumber()));
 var prev = acc.getBalance();
 if (prev.compareTo(req.amount()) < 0) throw new InsufficientFundsException("Insufficient balance");
 var next = prev.subtract(req.amount());
 acc.setBalance(next);
 var saved = accRepo.save(acc);
 log(saved, "DEBIT", req.amount(), prev, next, idOrNew(req.reference()));
 
 // Publish balance updated event
 var event = new AccountEvent("BALANCE_UPDATED", saved.getAccountNumber(), saved.getBalance(),
     saved.getCurrency(), saved.getUser().getId(), LocalDateTime.now(), idOrNew(req.reference()));
 eventPublisher.publishAccountEvent(event);
 
 return new BalanceResponse(saved.getAccountNumber(), saved.getCurrency(), saved.getBalance());
}

@Override
@Transactional
public AccountResponse deposit(Long accountId, DepositWithdrawRequest req) {
 var acc = accRepo.findById(accountId)
   .orElseThrow(() -> new NotFoundException("Account not found with ID: " + accountId));
 var prev = acc.getBalance();
 var next = prev.add(req.amount());
 acc.setBalance(next);
 var saved = accRepo.save(acc);
 log(saved, "DEPOSIT", req.amount(), prev, next, idOrNew(req.reference()));
 
 // Publish balance updated event
 var event = new AccountEvent("BALANCE_UPDATED", saved.getAccountNumber(), saved.getBalance(),
     saved.getCurrency(), saved.getUser().getId(), LocalDateTime.now(), idOrNew(req.reference()));
 eventPublisher.publishAccountEvent(event);
 
 return map(saved);
}

@Override
@Transactional
public AccountResponse withdraw(Long accountId, DepositWithdrawRequest req) {
 var acc = accRepo.findById(accountId)
   .orElseThrow(() -> new NotFoundException("Account not found with ID: " + accountId));
 var prev = acc.getBalance();
 if (prev.compareTo(req.amount()) < 0) throw new InsufficientFundsException("Insufficient balance");
 var next = prev.subtract(req.amount());
 acc.setBalance(next);
 var saved = accRepo.save(acc);
 log(saved, "WITHDRAW", req.amount(), prev, next, idOrNew(req.reference()));
 
 // Publish balance updated event
 var event = new AccountEvent("BALANCE_UPDATED", saved.getAccountNumber(), saved.getBalance(),
     saved.getCurrency(), saved.getUser().getId(), LocalDateTime.now(), idOrNew(req.reference()));
 eventPublisher.publishAccountEvent(event);
 
 return map(saved);
}

@Override @Transactional
public void transfer(TransferRequest req) {
 if (req.fromAccount().equals(req.toAccount())) throw new IllegalArgumentException("from and to cannot be same");

 var from = accRepo.findByAccountNumber(req.fromAccount())
   .orElseThrow(() -> new NotFoundException("From account not found"));
 var to = accRepo.findByAccountNumber(req.toAccount())
   .orElseThrow(() -> new NotFoundException("To account not found"));
 // debit
 var prevFrom = from.getBalance();
 if (prevFrom.compareTo(req.amount()) < 0) throw new InsufficientFundsException("Insufficient balance");
 from.setBalance(prevFrom.subtract(req.amount()));
 accRepo.save(from);
 log(from, "TRANSFER_DEBIT", req.amount(), prevFrom, from.getBalance(), idOrNew(req.reference()));
 
 // Publish balance updated event for debit
 var debitEvent = new AccountEvent("BALANCE_UPDATED", from.getAccountNumber(), from.getBalance(),
     from.getCurrency(), from.getUser().getId(), LocalDateTime.now(), idOrNew(req.reference()));
 eventPublisher.publishAccountEvent(debitEvent);
 
 // credit
 var prevTo = to.getBalance();
 to.setBalance(prevTo.add(req.amount()));
 accRepo.save(to);
 log(to, "TRANSFER_CREDIT", req.amount(), prevTo, to.getBalance(), idOrNew(req.reference()));
 
 // Publish balance updated event for credit
 var creditEvent = new AccountEvent("BALANCE_UPDATED", to.getAccountNumber(), to.getBalance(),
     to.getCurrency(), to.getUser().getId(), LocalDateTime.now(), idOrNew(req.reference()));
 eventPublisher.publishAccountEvent(creditEvent);
}

private String genAccNo() {
 return "AC" + UUID.randomUUID().toString().replace("-", "").substring(0,16).toUpperCase();
}
private String idOrNew(String ref) {
 return (ref == null || ref.isBlank()) ? UUID.randomUUID().toString() : ref;
}
private void log(BankAccountEntity acc, String action, BigDecimal amt, BigDecimal prev, BigDecimal next, String ref){
 var l = LogEntity.builder().account(acc).action(action).amount(amt)
   .prevBalance(prev).newBalance(next).reference(ref).build();
 logRepo.save(l);
}
private AccountResponse map(BankAccountEntity e){
 return new AccountResponse(e.getId(), e.getAccountNumber(), e.getAccountType(), e.getCurrency(),
   e.getBalance(), e.getStatus(), e.getUser().getId());
}
}