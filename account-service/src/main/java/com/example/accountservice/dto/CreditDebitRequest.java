package com.example.accountservice.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record CreditDebitRequest(
@NotBlank String accountNumber,
@NotNull @DecimalMin("0.01") BigDecimal amount,
String reference
) {}