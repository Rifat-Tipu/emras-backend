package com.emras.notification.controller;

import com.emras.notification.constant.ApiEndpointConstant;
import com.emras.notification.constant.SuccessMessages;
import com.emras.notification.dto.NotificationResponse;
import com.emras.notification.dto.PagedResponse;
import com.emras.notification.model.ApiResponse;
import com.emras.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "In-app notification management")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(ApiEndpointConstant.MY_NOTIFICATIONS)
    @Operation(summary = "Get my notifications with unread count")
    public ResponseEntity<ApiResponse<PagedResponse<NotificationResponse>>> getMyNotifications(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.NOTIFICATIONS_FETCHED,
                notificationService.getMyNotifications(userId,
                        PageRequest.of(page, size,
                                Sort.by("createdAt").descending())),
                HttpStatus.OK));
    }

    @PatchMapping(ApiEndpointConstant.MARK_READ)
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<ApiResponse<Void>> markRead(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {

        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.NOTIFICATION_READ, HttpStatus.OK));
    }

    @PatchMapping(ApiEndpointConstant.MARK_ALL_READ)
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<ApiResponse<Void>> markAllRead(
            @RequestHeader("X-User-Id") Long userId) {

        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.ALL_NOTIFICATIONS_READ, HttpStatus.OK));
    }
}