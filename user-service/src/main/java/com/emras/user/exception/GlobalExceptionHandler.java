package com.emras.user.exception;
import com.emras.user.constant.ErrorMessages;
import com.emras.user.model.ApiResponse;
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
    @ExceptionHandler(UserProfileNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleProfileNotFound(
            UserProfileNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.failure(ex.getMessage(), ex.getErrorCode(),
                        HttpStatus.NOT_FOUND, request.getRequestURI(), MDC.get("traceId")));
    }
    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleAddressNotFound(
            AddressNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.failure(ex.getMessage(), ex.getErrorCode(),
                        HttpStatus.NOT_FOUND, request.getRequestURI(), MDC.get("traceId")));
    }
    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserServiceException(
            UserServiceException ex, HttpServletRequest request) {
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
                ApiResponse.failure(ErrorMessages.INTERNAL_ERROR, "INTERNAL_ERROR",
                        HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI(),
                        MDC.get("traceId")));
    }
}