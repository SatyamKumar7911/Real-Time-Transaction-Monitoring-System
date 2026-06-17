package com.example.accountservice.dto;
import java.math.BigDecimal;
public record AccountResponse(
  Long id, String accountNumber, String accountType, String currency,
  BigDecimal balance, String status, Long userId
) {}