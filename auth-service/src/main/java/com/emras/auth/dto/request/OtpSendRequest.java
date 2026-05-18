package com.emras.auth.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
/** Request body for POST /api/v1/auth/login/otp/send */
public record OtpSendRequest(
        @NotBlank(message = "Phone number is required.")
        @Pattern(
                regexp = "^01[3-9]\\d{8}$",
                message = "Invalid phone number. Please use a valid Bangladeshi number (e.g. 01712345678)."
        )
        String phone
) {}