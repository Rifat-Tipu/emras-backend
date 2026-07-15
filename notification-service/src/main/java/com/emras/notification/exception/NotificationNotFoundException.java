package com.emras.notification.exception;

public class NotificationNotFoundException extends NotificationException {
    public NotificationNotFoundException() {
        super("Notification not found.", "NOTIFICATION_NOT_FOUND");
    }
}