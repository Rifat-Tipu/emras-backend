package com.emras.auth.exception;
import lombok.Getter;
/** Base exception for all Auth Service business logic errors */
@Getter
public class AuthException extends RuntimeException {
    private final String errorCode;

    public AuthException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}