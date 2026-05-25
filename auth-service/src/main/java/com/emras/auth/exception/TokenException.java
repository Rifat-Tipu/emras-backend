package com.emras.auth.exception;
public class TokenException extends AuthException {
    public TokenException(String message, String errorCode) {
        super(message, errorCode);
    }
}