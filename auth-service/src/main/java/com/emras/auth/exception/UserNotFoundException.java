package com.emras.auth.exception;
public class UserNotFoundException extends AuthException {
    public UserNotFoundException(String message) {
        super(message, "USER_NOT_FOUND");
    }
}