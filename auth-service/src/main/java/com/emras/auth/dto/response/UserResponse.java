package com.emras.auth.dto.response;
import java.time.Instant;
import java.util.Set;
/** Basic user info — returned after registration or token validation */
public record UserResponse(
        Long id,
        String email,
        String phone,
        boolean emailVerified,
        Set<String> roles,
        Instant createdAt
) {}