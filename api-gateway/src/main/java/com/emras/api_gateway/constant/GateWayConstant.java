package com.emras.api_gateway.constant;
/**
 * Constants for the API Gateway.
 */
public final class GateWayConstant {
    private  GateWayConstant() {}
    // ── Headers added by Gateway to downstream requests ───────────────────
    public static final String USER_ID_HEADER   = "X-User-Id";
    public static final String USER_ROLE_HEADER = "X-User-Role";
    public static final String TRACE_ID_HEADER  = "X-Trace-Id";
    // ── Auth header ────────────────────────────────────────────────────────
    public static final String AUTH_HEADER      = "Authorization";
    public static final String TOKEN_PREFIX     = "Bearer ";
    // ── Public paths — JWT validation is SKIPPED for these ────────────────
    public static final String[] PUBLIC_PATHS = {
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/login/otp/send",
            "/api/v1/auth/login/otp/verify",
            "/api/v1/auth/refresh",
            "/api/v1/auth/verify-email",
            "/api/v1/auth/verify-email/resend",
            "/api/v1/auth/password/forgot",
            "/api/v1/auth/password/reset",
            "/api/v1/auth/oauth2",
            "/api/v1/products",       // public browsing
            "/api/v1/categories",     // public category list
            "/actuator",
            "/swagger-ui",
            "/v3/api-docs"
    };
}