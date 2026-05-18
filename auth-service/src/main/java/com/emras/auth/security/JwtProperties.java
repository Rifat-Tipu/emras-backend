package com.emras.auth.security;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * Binds jwt.* properties from config-repo/auth-service.properties.
 * Injected wherever JWT generation/validation is needed.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** HMAC-SHA256 signing secret (minimum 256 bits = 32 chars) */
    private String secret;
    /** Access token TTL in milliseconds. Default: 900000 (15 min) */
    private long accessTokenExpirationMs = 900_000L;
    /** Refresh token TTL in milliseconds. Default: 604800000 (7 days) */
    private long refreshTokenExpirationMs = 604_800_000L;
}