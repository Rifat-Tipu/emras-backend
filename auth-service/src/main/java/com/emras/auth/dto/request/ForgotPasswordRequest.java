package com.emras.auth.dto.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
/** POST /api/v1/auth/password/forgot */
public record ForgotPasswordRequest(

        @NotBlank(message = "Email is required.")
        @Email(message = "Please enter a valid email address.")
        String email
) {}