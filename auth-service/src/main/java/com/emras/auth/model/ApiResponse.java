package com.emras.auth.model;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import java.time.Instant;
/**
 * Universal response wrapper for ALL Auth Service API endpoints.
 *
 * Every controller method returns: ResponseEntity<ApiResponse<T>>
 *
 * JSON output example (success):
 * {
 *   "success": true,
 *   "status": 200,
 *   "message": "Logged in successfully.",
 *   "data": { "accessToken": "..." },
 *   "timestamp": "2025-01-01T10:00:00Z"
 * }
 *
 * JSON output example (error):
 * {
 *   "success": false,
 *   "status": 401,
 *   "message": "Your session has expired.",
 *   "errorCode": "TOKEN_EXPIRED",
 *   "timestamp": "2025-01-01T10:00:00Z",
 *   "path": "/api/v1/auth/refresh",
 *   "traceId": "abc123"
 * }
 *
 * Note: @JsonInclude(NON_NULL) omits null fields from JSON automatically.
 * So 'data' is absent in error responses, and 'errorCode'/'path'/'traceId'
 * are absent in success responses.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ApiResponse<T> {
    /** true = success response, false = error response */
    private final boolean success;
    /** HTTP status code as int (e.g. 200, 201, 404) */
    private final int status;
    /** Human-readable message safe to display to the end user */
    private final String message;
    /**
     * Stable machine-readable error code for programmatic client handling.
     * Examples: "TOKEN_EXPIRED", "VALIDATION_FAILED", "ACCOUNT_LOCKED"
     * Always null on success responses (omitted from JSON).
     */
    private final String errorCode;
    /**
     * Response payload — present only on success responses.
     * Null on all error responses (omitted from JSON).
     */
    private final T data;
    /** UTC timestamp of when this response was created */
    private final Instant timestamp;
    /**
     * Request path that produced this response.
     * Populated from HttpServletRequest in the exception handler.
     * Null when not provided (omitted from JSON).
     */
    private final String path;
    /**
     * Distributed trace ID for correlating logs across microservices.
     * Populated from MDC by the exception handler or JWT filter.
     * Null when not available (omitted from JSON).
     */
    private final String traceId;
    // ══ Static factory methods — ALWAYS use these, never the raw @Builder ══
    /**
     * Success response WITH a data payload.
     * Example:
     *   return ApiResponse.success(SuccessMessages.LOGGED_IN, tokenDto, HttpStatus.OK);
     */
    public static <T> ApiResponse<T> success(String message, T data, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(status.value())
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }
    /**
     * Success response WITHOUT a data payload.
     * Use for: logout, delete operations, send-OTP, etc.
     * Example:
     *   return ApiResponse.success(SuccessMessages.LOGGED_OUT, HttpStatus.OK);
     */
    public static <T> ApiResponse<T> success(String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(status.value())
                .message(message)
                .timestamp(Instant.now())
                .build();
    }
    /**
     * Error response WITH path and traceId.
     * Use in @RestControllerAdvice where HttpServletRequest is available.
     * Example:
     *   ApiResponse.failure(ErrorMessages.TOKEN_EXPIRED, "TOKEN_EXPIRED",
     *                       HttpStatus.UNAUTHORIZED, "/api/v1/auth/refresh", traceId);
     */
    public static <T> ApiResponse<T> failure(
            String message,
            String errorCode,
            HttpStatus status,
            String path,
            String traceId) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status.value())
                .message(message)
                .errorCode(errorCode)
                .timestamp(Instant.now())
                .path(path)
                .traceId(traceId)
                .build();
    }
    /**
     * Error response WITHOUT path and traceId.
     * Use inside Spring Security filters or AuthenticationEntryPoint
     * where HttpServletRequest may not be cleanly accessible.
     * Example:
     *   ApiResponse.failure(ErrorMessages.AUTHENTICATION_REQUIRED,
     *                       "AUTHENTICATION_REQUIRED", HttpStatus.UNAUTHORIZED);
     */
    public static <T> ApiResponse<T> failure(
            String message,
            String errorCode,
            HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status.value())
                .message(message)
                .errorCode(errorCode)
                .timestamp(Instant.now())
                .build();
    }
}