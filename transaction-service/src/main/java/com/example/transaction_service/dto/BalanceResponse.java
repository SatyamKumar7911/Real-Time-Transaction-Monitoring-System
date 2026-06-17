package com.example.transaction_service.dto;
import java.math.BigDecimal;
public record BalanceResponse(String accountNumber, String currency, BigDecimal balance) {}