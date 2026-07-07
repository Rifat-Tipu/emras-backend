package com.emras.order.kafka.producer;
import com.emras.order.constant.KafkaTopic;
import com.emras.order.entity.Order;
import com.emras.order.entity.OrderItem;
import com.emras.order.entity.OutboxEvent;
import com.emras.order.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
/**
 * Writes order events to the Outbox table.
 * Must be called inside @Transactional — same DB commit as order update.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {
    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper          objectMapper;
    /**
     * Published when order is first created.
     * Consumed by Inventory Service to reserve stock.
     * One event per order item (one SKU per event).
     */
    public void publishOrderCreated(Order order, OrderItem item) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId",          order.getId());
        payload.put("userId",           order.getUserId());
        payload.put("sku",              item.getSku());
        payload.put("quantity",         item.getQuantity());
        payload.put("productVariantId", item.getProductVariantId());
        writeToOutbox(KafkaTopic.ORDER_CREATED, String.valueOf(order.getId()), payload);
    }
    /**
     * Published when inventory confirms reservation.
     * Consumed by Payment Service to initiate payment.
     */
    public void publishOrderConfirmed(Order order) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId",       order.getId());
        payload.put("userId",        order.getUserId());
        payload.put("totalAmount",   order.getTotalAmount());
        payload.put("paymentMethod", order.getPaymentMethod().name());
        writeToOutbox(KafkaTopic.ORDER_CONFIRMED, String.valueOf(order.getId()), payload);
    }
    /**
     * Published when order is cancelled — triggers inventory release.
     * One event per order item.
     */
    public void publishOrderCancelled(Order order, OrderItem item, String reason) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId",  order.getId());
        payload.put("userId",   order.getUserId());
        payload.put("sku",      item.getSku());
        payload.put("quantity", item.getQuantity());
        payload.put("reason",   reason);
        writeToOutbox(KafkaTopic.ORDER_CANCELLED, String.valueOf(order.getId()), payload);
    }
    private void writeToOutbox(String topic, String key, Map<String, Object> payload) {
        try {
            String eventId = UUID.randomUUID().toString();
            payload.put("eventId",    eventId);
            payload.put("occurredAt", Instant.now().toString());

            outboxRepository.save(OutboxEvent.builder()
                    .eventId(eventId)
                    .topic(topic)
                    .messageKey(key)
                    .payload(objectMapper.writeValueAsString(payload))
                    .status(OutboxEvent.OutboxStatus.PENDING)
                    .build());

            log.debug("Event written to outbox: topic={} eventId={}", topic, eventId);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize outbox event: {}", e.getMessage());
            throw new RuntimeException("Event serialization failed", e);
        }
    }
}