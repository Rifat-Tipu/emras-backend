package com.emras.notification.service;

import com.emras.notification.dto.NotificationResponse;
import com.emras.notification.dto.PagedResponse;
import com.emras.notification.entity.Notification;
import com.emras.notification.entity.ProcessedEvent;
import com.emras.notification.repository.NotificationRepository;
import com.emras.notification.repository.ProcessedEventRepository;
import com.emras.notification.sender.EmailSender;
import com.emras.notification.sender.SmsSender;
import com.emras.notification.template.NotificationTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository   notificationRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final EmailSender              emailSender;
    private final SmsSender               smsSender;

    private static final String CONSUMER_GROUP = "notification-service-group";

    // ── Idempotency ───────────────────────────────────────────────────────

    public boolean isDuplicate(String eventId) {
        return processedEventRepository
                .existsByEventIdAndConsumerGroup(eventId, CONSUMER_GROUP);
    }

    @Transactional
    public void markProcessed(String eventId) {
        processedEventRepository.save(ProcessedEvent.builder()
                .eventId(eventId)
                .consumerGroup(CONSUMER_GROUP)
                .build());
    }

    // ── Send notifications ────────────────────────────────────────────────

    @Transactional
    public void sendWelcome(Long userId, String email, String firstName) {
        NotificationTemplates.NotificationContent content =
                NotificationTemplates.welcome(firstName);

        boolean sent = emailSender.send(email, content.title(), content.body());

        saveNotification(userId, content, null, null,
                Notification.NotificationChannel.EMAIL, sent);

        // Also log as in-app notification
        saveNotification(userId, content, null, null,
                Notification.NotificationChannel.IN_APP, true);

        log.info("Welcome notification sent to userId={}", userId);
    }

    @Transactional
    public void sendOrderConfirmed(Long userId, String email, String phone,
                                   Long orderId, java.math.BigDecimal amount) {
        NotificationTemplates.NotificationContent content =
                NotificationTemplates.orderConfirmed(orderId, amount);

        boolean emailSent = emailSender.send(email, content.title(), content.body());
        boolean smsSent   = smsSender.send(phone,
                "Your Emras order #" + orderId + " confirmed! Amount: ৳" + amount);

        saveNotification(userId, content, orderId, "ORDER",
                Notification.NotificationChannel.EMAIL, emailSent);
        saveNotification(userId, content, orderId, "ORDER",
                Notification.NotificationChannel.SMS, smsSent);
        saveNotification(userId, content, orderId, "ORDER",
                Notification.NotificationChannel.IN_APP, true);
    }

    @Transactional
    public void sendPaymentSuccess(Long userId, String email, String phone,
                                   Long orderId, java.math.BigDecimal amount,
                                   String transactionId, String method) {
        NotificationTemplates.NotificationContent content =
                NotificationTemplates.paymentSuccess(orderId, amount, transactionId, method);

        boolean emailSent = emailSender.send(email, content.title(), content.body());
        boolean smsSent   = smsSender.send(phone,
                "Payment of ৳" + amount + " received for Emras order #" + orderId
                        + ". TxID: " + transactionId);

        saveNotification(userId, content, orderId, "PAYMENT",
                Notification.NotificationChannel.EMAIL, emailSent);
        saveNotification(userId, content, orderId, "PAYMENT",
                Notification.NotificationChannel.SMS, smsSent);
        saveNotification(userId, content, orderId, "PAYMENT",
                Notification.NotificationChannel.IN_APP, true);
    }

    @Transactional
    public void sendPaymentFailed(Long userId, String email,
                                  Long orderId, String reason) {
        NotificationTemplates.NotificationContent content =
                NotificationTemplates.paymentFailed(orderId, reason);

        boolean sent = emailSender.send(email, content.title(), content.body());

        saveNotification(userId, content, orderId, "PAYMENT",
                Notification.NotificationChannel.EMAIL, sent);
        saveNotification(userId, content, orderId, "PAYMENT",
                Notification.NotificationChannel.IN_APP, true);
    }

    @Transactional
    public void sendOrderCancelled(Long userId, String email,
                                   Long orderId, String reason) {
        NotificationTemplates.NotificationContent content =
                NotificationTemplates.orderCancelled(orderId, reason);

        boolean sent = emailSender.send(email, content.title(), content.body());

        saveNotification(userId, content, orderId, "ORDER",
                Notification.NotificationChannel.EMAIL, sent);
        saveNotification(userId, content, orderId, "ORDER",
                Notification.NotificationChannel.IN_APP, true);
    }

    @Transactional
    public void sendPaymentRefunded(Long userId, String email,
                                    Long orderId, java.math.BigDecimal amount) {
        NotificationTemplates.NotificationContent content =
                NotificationTemplates.paymentRefunded(orderId, amount);

        boolean sent = emailSender.send(email, content.title(), content.body());

        saveNotification(userId, content, orderId, "PAYMENT",
                Notification.NotificationChannel.EMAIL, sent);
        saveNotification(userId, content, orderId, "PAYMENT",
                Notification.NotificationChannel.IN_APP, true);
    }

    @Transactional
    public void sendLowStockAlert(String adminEmail, String sku,
                                  int currentStock, int threshold) {
        NotificationTemplates.NotificationContent content =
                NotificationTemplates.lowStockAlert(sku, currentStock, threshold);

        boolean sent = emailSender.send(adminEmail, content.title(), content.body());

        // null userId = admin notification
        saveNotification(null, content, null, "INVENTORY",
                Notification.NotificationChannel.EMAIL, sent);
    }

    @Transactional
    public void sendOutOfStockAlert(String adminEmail, String sku) {
        NotificationTemplates.NotificationContent content =
                NotificationTemplates.outOfStockAlert(sku);

        boolean sent = emailSender.send(adminEmail, content.title(), content.body());

        saveNotification(null, content, null, "INVENTORY",
                Notification.NotificationChannel.EMAIL, sent);
    }

    // ── User-facing query methods ─────────────────────────────────────────

    @Transactional(readOnly = true)
    public PagedResponse<NotificationResponse> getMyNotifications(
            Long userId, Pageable pageable) {
        Page<Notification> page = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable);

        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(userId);

        return new PagedResponse<>(
                page.getContent().stream().map(this::toResponse).toList(),
                page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(),
                page.isLast(), unreadCount);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            if (n.getUserId().equals(userId)) {
                n.setIsRead(true);
                notificationRepository.save(n);
            }
        });
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllReadByUserId(userId);
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private void saveNotification(Long userId,
                                  NotificationTemplates.NotificationContent content,
                                  Long referenceId, String referenceType,
                                  Notification.NotificationChannel channel,
                                  boolean sent) {
        notificationRepository.save(Notification.builder()
                .userId(userId)
                .type(content.type())
                .channel(channel)
                .title(content.title())
                .body(content.body())
                .referenceId(referenceId)
                .referenceType(referenceType)
                .isRead(false)
                .isSent(sent)
                .build());
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(), n.getUserId(), n.getType(), n.getChannel(),
                n.getTitle(), n.getBody(), n.getReferenceId(),
                n.getReferenceType(), n.getIsRead(), n.getIsSent(),
                n.getCreatedAt());
    }
}