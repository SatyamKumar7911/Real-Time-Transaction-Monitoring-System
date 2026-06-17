package com.example.accountservice.dto;
import jakarta.validation.constraints.*;
public record CreateUserRequest(
@NotBlank String name,
@Email @NotBlank String email,
@Pattern(regexp="^[0-9+\\-]{8,15}$", message="invalid phone") String phone
) {}
