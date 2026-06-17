package com.example.accountservice.dto;
import java.math.BigDecimal;
public record BalanceResponse(String accountNumber, String currency, BigDecimal balance) {}