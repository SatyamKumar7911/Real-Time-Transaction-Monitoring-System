package com.example.transaction_service.dto;

import java.math.BigDecimal;

/**
 * Response DTO for account balance information
 */
public record AccountBalanceResponse(
        String accountNumber,
        String currency,
        BigDecimal balance
) {}