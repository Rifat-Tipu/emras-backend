package com.emras.auth.exception;
import com.emras.auth.constant.ErrorMessages;
import com.emras.auth.model.ApiResponse;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;
/**
 * Centralized exception handler for all Auth Service controllers.
 *
 * Every unhandled exception reaches here and is converted to
 * a consistent ApiResponse.failure() with appropriate HTTP status.
 *
 * TraceId from Micrometer MDC is included in every error response
 * so frontend can report it to support for debugging.
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final Tracer tracer;
    // ── Auth-specific exceptions ──────────────────────────────────────────
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthException(
            AuthException ex, HttpServletRequest request) {

        HttpStatus status = resolveStatus(ex.getErrorCode());
        log.warn("AuthException [{}]: {} — path: {}", ex.getErrorCode(), ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(
                ApiResponse.failure(ex.getMessage(), ex.getErrorCode(), status,
                        request.getRequestURI(), getTraceId())
        );
    }
    // ── Spring Security exceptions ────────────────────────────────────────
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.failure(ErrorMessages.INVALID_CREDENTIALS, "INVALID_CREDENTIALS",
                        HttpStatus.UNAUTHORIZED, request.getRequestURI(), getTraceId())
        );
    }
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiResponse<Void>> handleLocked(
            LockedException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiResponse.failure(ErrorMessages.ACCOUNT_LOCKED, "ACCOUNT_LOCKED",
                        HttpStatus.FORBIDDEN, request.getRequestURI(), getTraceId())
        );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<Void>> handleDisabled(
            DisabledException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiResponse.failure(ErrorMessages.ACCOUNT_DISABLED, "ACCOUNT_DISABLED",
                        HttpStatus.FORBIDDEN, request.getRequestURI(), getTraceId())
        );
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiResponse.failure(ErrorMessages.ACCESS_DENIED, "ACCESS_DENIED",
                        HttpStatus.FORBIDDEN, request.getRequestURI(), getTraceId())
        );
    }
    // ── Validation exceptions ─────────────────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(" | "));

        String message = fieldErrors.isBlank() ? ErrorMessages.VALIDATION_FAILED : fieldErrors;

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.failure(message, "VALIDATION_FAILED",
                        HttpStatus.BAD_REQUEST, request.getRequestURI(), getTraceId())
        );
    }
    // ── Catch-all ─────────────────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(
            Exception ex, HttpServletRequest request) {

        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.failure(ErrorMessages.INTERNAL_ERROR, "INTERNAL_ERROR",
                        HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI(), getTraceId())
        );
    }
    // ── Helpers ───────────────────────────────────────────────────────────
    /**
     * Retrieves the current trace ID from Micrometer's MDC.
     * This is automatically populated by micrometer-tracing-bridge-brave.
     * Returns null if tracing is not available (omitted from JSON by @JsonInclude).
     */
    private String getTraceId() {
        try {
            if (tracer != null && tracer.currentSpan() != null) {
                return tracer.currentSpan().context().traceId();
            }
        } catch (Exception e) {
            log.debug("Could not retrieve trace ID: {}", e.getMessage());
        }
        return MDC.get("traceId"); // fallback to MDC
    }
    /** Maps error codes to HTTP status codes */
    private HttpStatus resolveStatus(String errorCode) {
        return switch (errorCode) {
            case "USER_NOT_FOUND", "ROLE_NOT_FOUND"      -> HttpStatus.NOT_FOUND;
            case "TOKEN_EXPIRED", "REFRESH_TOKEN_EXPIRED",
                 "TOKEN_INVALID", "TOKEN_BLACKLISTED",
                 "REFRESH_TOKEN_INVALID"                  -> HttpStatus.UNAUTHORIZED;
            case "ACCOUNT_LOCKED", "ACCOUNT_DISABLED",
                 "ACCESS_DENIED", "EMAIL_NOT_VERIFIED"    -> HttpStatus.FORBIDDEN;
            case "EMAIL_ALREADY_EXISTS",
                 "PHONE_ALREADY_EXISTS",
                 "EMAIL_ALREADY_VERIFIED"                 -> HttpStatus.CONFLICT;
            case "OTP_INVALID", "OTP_EXPIRED",
                 "OTP_MAX_ATTEMPTS", "PASSWORD_MISMATCH"  -> HttpStatus.BAD_REQUEST;
            default                                       -> HttpStatus.BAD_REQUEST;
        };
    }
}