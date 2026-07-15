package com.emras.notification.dto;

import com.emras.notification.entity.Notification;

import java.time.Instant;

public record NotificationResponse(
        Long                          id,
        Long                          userId,
        Notification.NotificationType type,
        Notification.NotificationChannel channel,
        String                        title,
        String                        body,
        Long                          referenceId,
        String                        referenceType,
        Boolean                       isRead,
        Boolean                       isSent,
        Instant                       createdAt
) {}