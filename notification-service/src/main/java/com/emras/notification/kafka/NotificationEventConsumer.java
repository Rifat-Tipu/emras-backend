package com.emras.notification.kafka;

import com.emras.notification.constant.KafkaTopic;
import com.emras.notification.service.NotificationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Consumes events from all services and triggers notifications.
 *
 * Note: user email and phone are embedded in events where needed.
 * In production, you could also fetch user details via FeignClient.
 *
 * For local dev without real user emails in events, we log to console.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper        objectMapper;

    @Value("${notification.admin-email:admin@emras.com.bd}")
    private String adminEmail;

    @KafkaListener(topics = KafkaTopic.USER_REGISTERED,
            groupId = "${spring.kafka.consumer.group-id}")
    public void onUserRegistered(String message, Acknowledgment acknowledgment) {
        try {
            JsonNode payload  = objectMapper.readTree(message);
            String   eventId  = extractEventId(payload);

            if (notificationService.isDuplicate(eventId)) {
                acknowledgment.acknowledge();
                return;
            }

            Long   userId    = payload.get("userId").asLong();
            String email     = payload.path("email").asText("");
            String firstName = payload.path("firstName").asText("Customer");

            log.info("Sending welcome notification to userId={}", userId);
            notificationService.sendWelcome(userId, email, firstName);
            notificationService.markProcessed(eventId);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process user.registered: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopic.ORDER_CONFIRMED,
            groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderConfirmed(String message, Acknowledgment acknowledgment) {
        try {
            JsonNode   payload  = objectMapper.readTree(message);
            String     eventId  = extractEventId(payload);

            if (notificationService.isDuplicate(eventId)) {
                acknowledgment.acknowledge();
                return;
            }

            Long       orderId  = payload.get("orderId").asLong();
            Long       userId   = payload.get("userId").asLong();
            BigDecimal amount   = new BigDecimal(payload.get("totalAmount").asText());
            String     email    = payload.path("email").asText(adminEmail);
            String     phone    = payload.path("phone").asText("");

            notificationService.sendOrderConfirmed(userId, email, phone, orderId, amount);
            notificationService.markProcessed(eventId);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process order.confirmed: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopic.PAYMENT_SUCCESS,
            groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentSuccess(String message, Acknowledgment acknowledgment) {
        try {
            JsonNode   payload       = objectMapper.readTree(message);
            String     eventId       = extractEventId(payload);

            if (notificationService.isDuplicate(eventId)) {
                acknowledgment.acknowledge();
                return;
            }

            Long       orderId       = payload.get("orderId").asLong();
            Long       userId        = payload.get("userId").asLong();
            BigDecimal amount        = new BigDecimal(payload.get("amount").asText());
            String     transactionId = payload.path("transactionId").asText("");
            String     method        = payload.path("method").asText("");
            String     email         = payload.path("email").asText(adminEmail);
            String     phone         = payload.path("phone").asText("");

            notificationService.sendPaymentSuccess(
                    userId, email, phone, orderId, amount, transactionId, method);
            notificationService.markProcessed(eventId);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process payment.success: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopic.PAYMENT_FAILED,
            groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentFailed(String message, Acknowledgment acknowledgment) {
        try {
            JsonNode payload = objectMapper.readTree(message);
            String   eventId = extractEventId(payload);

            if (notificationService.isDuplicate(eventId)) {
                acknowledgment.acknowledge();
                return;
            }

            Long   orderId = payload.get("orderId").asLong();
            Long   userId  = payload.get("userId").asLong();
            String reason  = payload.path("reason").asText("Unknown reason");
            String email   = payload.path("email").asText(adminEmail);

            notificationService.sendPaymentFailed(userId, email, orderId, reason);
            notificationService.markProcessed(eventId);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process payment.failed: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopic.ORDER_CANCELLED,
            groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderCancelled(String message, Acknowledgment acknowledgment) {
        try {
            JsonNode payload = objectMapper.readTree(message);
            String   eventId = extractEventId(payload);

            if (notificationService.isDuplicate(eventId)) {
                acknowledgment.acknowledge();
                return;
            }

            Long   orderId = payload.get("orderId").asLong();
            Long   userId  = payload.get("userId").asLong();
            String reason  = payload.path("reason").asText("Order cancelled");
            String email   = payload.path("email").asText(adminEmail);

            notificationService.sendOrderCancelled(userId, email, orderId, reason);
            notificationService.markProcessed(eventId);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process order.cancelled: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopic.PAYMENT_REFUNDED,
            groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentRefunded(String message, Acknowledgment acknowledgment) {
        try {
            JsonNode   payload = objectMapper.readTree(message);
            String     eventId = extractEventId(payload);

            if (notificationService.isDuplicate(eventId)) {
                acknowledgment.acknowledge();
                return;
            }

            Long       orderId      = payload.get("orderId").asLong();
            Long       userId       = payload.get("userId").asLong();
            BigDecimal refundAmount = new BigDecimal(payload.get("refundAmount").asText());
            String     email        = payload.path("email").asText(adminEmail);

            notificationService.sendPaymentRefunded(userId, email, orderId, refundAmount);
            notificationService.markProcessed(eventId);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process payment.refunded: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopic.INVENTORY_LOW_STOCK,
            groupId = "${spring.kafka.consumer.group-id}")
    public void onLowStock(String message, Acknowledgment acknowledgment) {
        try {
            JsonNode payload      = objectMapper.readTree(message);
            String   eventId      = extractEventId(payload);

            if (notificationService.isDuplicate(eventId)) {
                acknowledgment.acknowledge();
                return;
            }

            String sku          = payload.get("sku").asText();
            int    currentStock = payload.get("currentStock").asInt();
            int    threshold    = payload.get("threshold").asInt();

            notificationService.sendLowStockAlert(adminEmail, sku, currentStock, threshold);
            notificationService.markProcessed(eventId);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process inventory.low-stock: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopic.INVENTORY_OUT_OF_STOCK,
            groupId = "${spring.kafka.consumer.group-id}")
    public void onOutOfStock(String message, Acknowledgment acknowledgment) {
        try {
            JsonNode payload = objectMapper.readTree(message);
            String   eventId = extractEventId(payload);

            if (notificationService.isDuplicate(eventId)) {
                acknowledgment.acknowledge();
                return;
            }

            String sku = payload.get("sku").asText();

            notificationService.sendOutOfStockAlert(adminEmail, sku);
            notificationService.markProcessed(eventId);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process inventory.out-of-stock: {}", e.getMessage(), e);
        }
    }

    private String extractEventId(JsonNode payload) {
        if (!payload.has("eventId") || payload.get("eventId").isNull()) {
            return "fallback-" + System.currentTimeMillis();
        }
        return payload.get("eventId").asText();
    }
}