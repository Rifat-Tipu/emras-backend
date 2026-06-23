package com.emras.inventory.exception;
import com.emras.inventory.model.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InventoryItemNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            InventoryItemNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.failure(ex.getMessage(), ex.getErrorCode(),
                        HttpStatus.NOT_FOUND, request.getRequestURI(), MDC.get("traceId")));
    }
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientStock(
            InsufficientStockException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse.failure(ex.getMessage(), ex.getErrorCode(),
                        HttpStatus.CONFLICT, request.getRequestURI(), MDC.get("traceId")));
    }
    /**
     * Caught when two concurrent requests try to update the same inventory row.
     * This means a race condition was detected and handled safely by optimistic locking.
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLock(
            ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
        log.warn("Optimistic locking conflict on inventory item: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse.failure(
                        "Stock was updated by another request. Please try again.",
                        "CONCURRENT_UPDATE",
                        HttpStatus.CONFLICT, request.getRequestURI(), MDC.get("traceId")));
    }
    @ExceptionHandler(InventoryException.class)
    public ResponseEntity<ApiResponse<Void>> handleInventoryException(
            InventoryException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.failure(ex.getMessage(), ex.getErrorCode(),
                        HttpStatus.BAD_REQUEST, request.getRequestURI(), MDC.get("traceId")));
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