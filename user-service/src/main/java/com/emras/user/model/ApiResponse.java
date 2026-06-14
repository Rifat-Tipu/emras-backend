package com.emras.user.model;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import java.time.Instant;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ApiResponse<T> {
    private final boolean success;
    private final int     status;
    private final String  message;
    private final String  errorCode;
    private final T       data;
    private final Instant timestamp;
    private final String  path;
    private final String  traceId;

    public static <T> ApiResponse<T> success(String message, T data, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(true).status(status.value())
                .message(message).data(data)
                .timestamp(Instant.now()).build();
    }
    public static <T> ApiResponse<T> success(String message, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(true).status(status.value())
                .message(message).timestamp(Instant.now()).build();
    }
    public static <T> ApiResponse<T> failure(String message, String errorCode,
                                             HttpStatus status, String path, String traceId) {
        return ApiResponse.<T>builder()
                .success(false).status(status.value())
                .message(message).errorCode(errorCode)
                .path(path).traceId(traceId)
                .timestamp(Instant.now()).build();
    }
    public static <T> ApiResponse<T> failure(String message, String errorCode, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(false).status(status.value())
                .message(message).errorCode(errorCode)
                .timestamp(Instant.now()).build();
    }
}