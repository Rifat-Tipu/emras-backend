package com.emras.order.exception;
import com.emras.order.model.ApiResponse;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            OrderNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.failure(ex.getMessage(), ex.getErrorCode(),
                        HttpStatus.NOT_FOUND, request.getRequestURI(), MDC.get("traceId")));
    }
    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ApiResponse<Void>> handleOrderException(
            OrderException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.failure(ex.getMessage(), ex.getErrorCode(),
                        HttpStatus.BAD_REQUEST, request.getRequestURI(), MDC.get("traceId")));
    }
    /**
     * Handles FeignClient errors — when Product Service or User Service
     * returns an error response, Feign wraps it in FeignException.
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<Void>> handleFeignException(
            FeignException ex, HttpServletRequest request) {
        log.error("FeignClient error: status={} message={}", ex.status(), ex.getMessage());

        HttpStatus status = ex.status() == 404
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_GATEWAY;

        return ResponseEntity.status(status).body(
                ApiResponse.failure(
                        "Failed to communicate with a dependent service.",
                        "DOWNSTREAM_ERROR",
                        status, request.getRequestURI(), MDC.get("traceId")));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(" | "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.failure(message, "VALIDATION_FAILED",
                        HttpStatus.BAD_REQUEST, request.getRequestURI(), MDC.get("traceId")));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.failure("An unexpected error occurred.",
                        "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR,
                        request.getRequestURI(), MDC.get("traceId")));
    }
}