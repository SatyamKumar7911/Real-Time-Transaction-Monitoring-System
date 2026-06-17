package com.example.accountservice.controller;
import com.example.accountservice.dto.*;
import com.example.accountservice.entity.UserEntity;
import com.example.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AccountController {
private final AccountService service;

// Users
@PostMapping("/users")
public ResponseEntity<UserEntity> createUser(@Valid @RequestBody CreateUserRequest req){
    return ResponseEntity.ok(service.createUser(req));
}

@GetMapping("/users")
public ResponseEntity<List<UserEntity>> getAllUsers(){
    return ResponseEntity.ok(service.getAllUsers());
}

@GetMapping("/users/{id}")
public ResponseEntity<UserEntity> getUserById(@PathVariable Long id){
    return ResponseEntity.ok(service.getUserById(id));
}

@PutMapping("/users/{id}")
public ResponseEntity<UserEntity> updateUser(@PathVariable Long id, @Valid @RequestBody CreateUserRequest req){
    return ResponseEntity.ok(service.updateUser(id, req));
}

@DeleteMapping("/users/{id}")
public ResponseEntity<Void> deleteUser(@PathVariable Long id){
    service.deleteUser(id);
    return ResponseEntity.noContent().build();
}

// Accounts
@PostMapping("/accounts")
public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest req){
    return ResponseEntity.ok(service.createAccount(req));
}

@GetMapping("/accounts")
public ResponseEntity<String> listAccounts(){
    return ResponseEntity.ok("✅ Account Service - Protected endpoint working! You are authenticated.");
}

@GetMapping("/accounts/{accountNumber}")
public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountNumber){
    return ResponseEntity.ok(service.getAccountByNumber(accountNumber));
}

@GetMapping("/accounts/id/{id}")
public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long id){
    return ResponseEntity.ok(service.getAccountById(id));
}

@GetMapping("/accounts/{accountNumber}/balance")
public ResponseEntity<BalanceResponse> getBalance(@PathVariable String accountNumber){
    return ResponseEntity.ok(service.getBalance(accountNumber));
}

@GetMapping("/accounts/user/{userId}")
public ResponseEntity<List<AccountResponse>> getUserAccounts(@PathVariable Long userId){
    return ResponseEntity.ok(service.getUserAccounts(userId));
}

// Money ops (to be called by Transaction Service later)
@PostMapping("/accounts/credit")
public ResponseEntity<BalanceResponse> credit(@Valid @RequestBody CreditDebitRequest req){
    return ResponseEntity.ok(service.credit(req));
}

@PostMapping("/accounts/debit")
public ResponseEntity<BalanceResponse> debit(@Valid @RequestBody CreditDebitRequest req){
    return ResponseEntity.ok(service.debit(req));
}

@PostMapping("/accounts/{id}/deposit")
public ResponseEntity<AccountResponse> deposit(@PathVariable Long id, @Valid @RequestBody DepositWithdrawRequest req){
    return ResponseEntity.ok(service.deposit(id, req));
}

@PostMapping("/accounts/{id}/withdraw")
public ResponseEntity<AccountResponse> withdraw(@PathVariable Long id, @Valid @RequestBody DepositWithdrawRequest req){
    return ResponseEntity.ok(service.withdraw(id, req));
}

@PostMapping("/accounts/transfer")
public ResponseEntity<Void> transfer(@Valid @RequestBody TransferRequest req){
    service.transfer(req);
    return ResponseEntity.ok().build();
}
}
