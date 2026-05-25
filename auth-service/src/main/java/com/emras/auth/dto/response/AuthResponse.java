package com.emras.auth.dto.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Set;
/**
 * Returned in ApiResponse.data after successful login or token refresh.
 * refreshToken is NOT included here — it is set as an HttpOnly cookie.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,     // access token TTL in seconds
        Long userId,
        String email,
        Set<String> roles
) {
    public static AuthResponse of(
            String accessToken,
            long expiresIn,
            Long userId,
            String email,
            Set<String> roles) {
        return new AuthResponse(accessToken, "Bearer", expiresIn, userId, email, roles);
    }
}