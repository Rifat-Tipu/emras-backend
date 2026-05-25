package com.emras.auth.security;
import com.emras.auth.constant.CacheKeys;
import com.emras.auth.constant.ErrorMessages;
import com.emras.auth.exception.TokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
/**
 * Centralized JWT operations for the Auth Service.
 *
 * Access tokens:
 *   - Short-lived (15 min)
 *   - Contain userId, email, roles, jti (unique ID)
 *   - Validated by API Gateway for all downstream services
 *
 * Refresh tokens:
 *   - Long-lived (7 days)
 *   - Stored as HttpOnly cookie on client
 *   - Whitelisted in Redis — revoked on rotation or logout
 *   - Also persisted in DB for audit trail
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtProperties jwtProperties;
    private final StringRedisTemplate redisTemplate;
    // ── Token Generation ──────────────────────────────────────────────────
    /**
     * Generates a signed JWT access token.
     *
     * @param userId  the user's database ID
     * @param email   the user's email
     * @param roles   comma-separated role names (e.g. "ROLE_CUSTOMER")
     * @return signed JWT string
     */
    public String generateAccessToken(Long userId, String email, String roles) {
        String jti = UUID.randomUUID().toString();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getAccessTokenExpirationMs());

        return Jwts.builder()
                .id(jti)
                .subject(email)
                .claim("userId", userId)
                .claim("roles", roles)
                .claim("type", "access")
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }
    /**
     * Generates a unique JTI string for a refresh token.
     * The actual refresh token stored in the cookie is this JTI value.
     * It is whitelisted in Redis and stored in the DB.
     */
    public String generateRefreshTokenJti() {
        return UUID.randomUUID().toString();
    }
    // ── Token Validation ──────────────────────────────────────────────────
    /**
     * Validates an access token.
     * Checks: signature, expiry, token type, not blacklisted in Redis.
     *
     * @param token raw JWT string (without "Bearer " prefix)
     * @throws TokenException with appropriate message if invalid
     */
    public Claims validateAccessToken(String token) {
        try {
            Claims claims = parseClaims(token);

            // Verify this is actually an access token (not a refresh token JTI)
            String type = claims.get("type", String.class);
            if (!"access".equals(type)) {
                throw new TokenException(ErrorMessages.TOKEN_INVALID, "TOKEN_INVALID");
            }

            // Check if blacklisted (i.e. user logged out)
            String jti = claims.getId();
            if (isBlacklisted(jti)) {
                throw new TokenException(ErrorMessages.TOKEN_BLACKLISTED, "TOKEN_BLACKLISTED");
            }

            return claims;

        } catch (ExpiredJwtException e) {
            log.debug("Access token expired: {}", e.getMessage());
            throw new TokenException(ErrorMessages.TOKEN_EXPIRED, "TOKEN_EXPIRED");
        } catch (JwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            throw new TokenException(ErrorMessages.TOKEN_INVALID, "TOKEN_INVALID");
        }
    }
    // ── Claims Extraction ─────────────────────────────────────────────────
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    public String extractRoles(String token) {
        return parseClaims(token).get("roles", String.class);
    }

    public String extractJti(String token) {
        return parseClaims(token).getId();
    }
    // ── Blacklist (logout) ────────────────────────────────────────────────
    /**
     * Blacklists an access token's JTI in Redis so it cannot be reused.
     * TTL is set to the remaining lifetime of the token.
     */
    public void blacklistToken(String token) {
        try {
            Claims claims = parseClaims(token);
            String jti = claims.getId();
            long remainingMs = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (remainingMs > 0) {
                redisTemplate.opsForValue().set(
                        CacheKeys.blacklistedToken(jti),
                        "blacklisted",
                        Duration.ofMillis(remainingMs)
                );
                log.debug("Token {} blacklisted for {}ms", jti, remainingMs);
            }
        } catch (JwtException e) {
            log.warn("Could not blacklist token (already invalid): {}", e.getMessage());
        }
    }

    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(CacheKeys.blacklistedToken(jti)));
    }
    // ── Private Helpers ───────────────────────────────────────────────────
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}