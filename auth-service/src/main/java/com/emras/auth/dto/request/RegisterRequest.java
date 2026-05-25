package com.emras.auth.dto.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
/**
 * Request body for POST /api/v1/auth/register
 */
public record RegisterRequest(

        @NotBlank(message = "Email is required.")
        @Email(message = "Please enter a valid email address.")
        String email,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters.")
        String password,
        /**
         * Bangladeshi phone number — optional at registration.
         * Format: 01x-xxxxxxxx (11 digits, starts with 01)
         */
        @Pattern(
                regexp = "^01[3-9]\\d{8}$",
                message = "Invalid phone number. Please use a valid Bangladeshi number (e.g. 01712345678)."
        )
        String phone
) {}