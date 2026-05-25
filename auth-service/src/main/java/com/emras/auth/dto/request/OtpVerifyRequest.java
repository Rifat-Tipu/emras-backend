package com.emras.auth.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
/** Request body for POST /api/v1/auth/login/otp/verify */
public record OtpVerifyRequest(

        @NotBlank(message = "Phone number is required.")
        @Pattern(regexp = "^01[3-9]\\d{8}$", message = "Invalid phone number.")
        String phone,

        @NotBlank(message = "OTP is required.")
        @Size(min = 6, max = 6, message = "OTP must be exactly 6 digits.")
        String otp
) {}