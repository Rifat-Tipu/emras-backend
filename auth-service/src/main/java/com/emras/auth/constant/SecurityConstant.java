package com.emras.auth.constant;
/**
 * Security-related constants for the Auth Service.
 * Numeric limits and header names referenced across security layer.
 */
public final class SecurityConstant {
    private SecurityConstant() {}
    // ── HTTP Headers ───────────────────────────────────────────────────────
    public static final String AUTH_HEADER         = "Authorization";
    public static final String TOKEN_PREFIX        = "Bearer ";
    public static final String TRACE_ID_HEADER     = "X-Trace-Id";
    public static final String CORRELATION_HEADER  = "X-Correlation-Id";
    public static final String USER_ID_HEADER      = "X-User-Id";   // added by Gateway
    public static final String USER_ROLE_HEADER    = "X-User-Role"; // added by Gateway
    // ── Roles ──────────────────────────────────────────────────────────────
    public static final String ROLE_CUSTOMER       = "ROLE_CUSTOMER";
    public static final String ROLE_ADMIN          = "ROLE_ADMIN";
    public static final String ROLE_STAFF          = "ROLE_STAFF";
    public static final String ROLE_VIEWER         = "ROLE_VIEWER";
    // ── Public Endpoints (no JWT required) ────────────────────────────────
    public static final String[] PUBLIC_ENDPOINTS = {
            ApiEndpointConstant.REGISTER,
            ApiEndpointConstant.LOGIN,
            ApiEndpointConstant.LOGIN_OTP_SEND,
            ApiEndpointConstant.LOGIN_OTP_VERIFY,
            ApiEndpointConstant.VERIFY_EMAIL,
            ApiEndpointConstant.FORGOT_PASSWORD,
            ApiEndpointConstant.RESET_PASSWORD,
            "/api/v1/auth/oauth2/**",
            "/oauth2/**",
            "/actuator/health",
            "/actuator/info",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };
    // ── Cookie names ───────────────────────────────────────────────────────
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";
}