package com.example.accountservice.dto;
import jakarta.validation.constraints.*;
public record CreateAccountRequest(
@NotNull Long userId,
@NotBlank String accountType,
@NotBlank String currency
) {}