package com.emras.api_gateway.filter;

import com.emras.api_gateway.constant.GateWayConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;

/**
 * JWT Authentication Filter for the MVC Gateway.
 *
 * Runs once per request BEFORE routing to any downstream service.
 *
 * Flow:
 *  1. Public path? → skip validation, forward request.
 *  2. Extract Bearer token from Authorization header.
 *  3. Validate the token (signature + expiry).
 *  4. Valid → add X-User-Id and X-User-Role headers → forward to service.
 *  5. Invalid → return 401 JSON response immediately.
 *
 * This is identical in style to auth-service's JwtAuthFilter.
 * No reactive code needed.
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // ── Step 1: Skip public paths ──────────────────────────────────────
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ── Step 2: Extract token ──────────────────────────────────────────
        String authHeader = request.getHeader(GateWayConstant.AUTH_HEADER);

        if (authHeader == null || !authHeader.startsWith(GateWayConstant.TOKEN_PREFIX)) {
            log.debug("No Bearer token for path: {}", path);
            writeUnauthorizedResponse(response, "Authentication is required.");
            return;
        }

        String token = authHeader.substring(GateWayConstant.TOKEN_PREFIX.length());

        // ── Step 3 & 4: Validate and add headers ──────────────────────────
        try {
            Claims claims = validateToken(token);

            String userId = claims.get("userId", Long.class).toString();
            String roles  = claims.get("roles", String.class);

            // Wrap the request to add custom headers
            // (HttpServletRequest headers are immutable — we use a wrapper)
            MutableHttpServletRequest mutableRequest =
                    new MutableHttpServletRequest(request);
            mutableRequest.addHeader(GateWayConstant.USER_ID_HEADER, userId);
            mutableRequest.addHeader(GateWayConstant.USER_ROLE_HEADER, roles);

            log.debug("JWT valid — userId={} roles={} path={}", userId, roles, path);
            filterChain.doFilter(mutableRequest, response);

        } catch (ExpiredJwtException e) {
            log.debug("Token expired for path: {}", path);
            writeUnauthorizedResponse(response, "Your session has expired. Please log in again.");
        } catch (JwtException e) {
            log.debug("Invalid token for path: {}", path);
            writeUnauthorizedResponse(response, "Invalid token.");
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private boolean isPublicPath(String path) {
        return Arrays.stream(GateWayConstant.PUBLIC_PATHS)
                .anyMatch(path::startsWith);
    }

    private void writeUnauthorizedResponse(HttpServletResponse response,
                                           String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String body = objectMapper.writeValueAsString(Map.of(
                "success",   false,
                "status",    401,
                "message",   message,
                "errorCode", "AUTHENTICATION_REQUIRED",
                "timestamp", Instant.now().toString()
        ));
        response.getWriter().write(body);
    }
}