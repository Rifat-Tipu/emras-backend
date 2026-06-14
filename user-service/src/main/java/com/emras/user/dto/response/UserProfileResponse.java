package com.emras.user.dto.response;
import com.emras.user.entity.UserProfile;
import java.time.Instant;
public record UserProfileResponse(
        Long   id,
        Long   userId,
        String firstName,
        String lastName,
        String phone,
        String avatarUrl,
        UserProfile.Language preferredLanguage,
        UserProfile.Gender   gender,
        Instant createdAt
) {}