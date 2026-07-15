package com.emras.notification.exception;

import lombok.Getter;

@Getter
public class NotificationException extends RuntimeException {
    private final String errorCode;

    public NotificationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}