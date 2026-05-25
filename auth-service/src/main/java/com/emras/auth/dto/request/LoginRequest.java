package com.emras.auth.dto.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
/** Request body for POST /api/v1/auth/login */
public record LoginRequest(
        @NotBlank(message = "Email is required.")
        @Email(message = "Please enter a valid email address.")
        String email,

        @NotBlank(message = "Password is required.")
        String password
) {}