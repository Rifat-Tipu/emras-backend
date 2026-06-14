package com.emras.user.dto.request;
import com.emras.user.entity.UserProfile;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
public record UpdateProfileRequest(
        @Size(max = 100, message = "First name must not exceed 100 characters.")
        String firstName,
        @Size(max = 100, message = "Last name must not exceed 100 characters.")
        String lastName,
        @Pattern(regexp = "^01[3-9]\\d{8}$",
                message = "Invalid phone number. Use a valid Bangladeshi number.")
        String phone,
        String avatarUrl,
        UserProfile.Language preferredLanguage,
        UserProfile.Gender gender
) {}