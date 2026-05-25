package com.emras.auth.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
/** POST /api/v1/auth/password/change (requires valid JWT) */
public record ChangePasswordRequest(

        @NotBlank(message = "Current password is required.")
        String currentPassword,

        @NotBlank(message = "New password is required.")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters.")
        String newPassword
) {}