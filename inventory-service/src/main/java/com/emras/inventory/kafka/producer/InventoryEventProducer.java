package com.emras.inventory.kafka.producer;

import com.emras.inventory.constant.KafkaTopic;
import com.emras.inventory.entity.OutboxEvent;
import com.emras.inventory.repository.OutboxEventRepository;
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
 * Writes events to the Outbox table instead of Kafka directly.
 *
 * This MUST be called inside the same @Transactional scope as the
 * inventory DB update. Both operations commit together or roll back together.
 *
 * The OutboxPublisherService scheduler then reads and publishes to Kafka separately.
 *
 * Before (broken):
 *   @Transactional
 *   void reserveStock() {
 *     db.save(item);           ← DB transaction
 *     kafka.send(event);       ← NOT in DB transaction — can fail independently
 *   }
 *
 * After (correct):
 *   @Transactional
 *   void reserveStock() {
 *     db.save(item);           ← DB transaction
 *     outbox.save(event);      ← SAME DB transaction — atomic with above
 *   }
 *   // Scheduler publishes to Kafka after commit — outside transaction
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventProducer {

    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper          objectMapper;

    public void publishReserved(Long orderId, String sku, int quantity) {
        Map<String, Object> payload = buildPayload(
                "orderId", orderId, "sku", sku, "quantity", quantity);
        writeToOutbox(KafkaTopic.INVENTORY_RESERVED, String.valueOf(orderId), payload);
    }

    public void publishReservationFailed(Long orderId, String sku,
                                         int requested, int available, String reason) {
        Map<String, Object> payload = buildPayload(
                "orderId", orderId, "sku", sku,
                "requested", requested, "available", available, "reason", reason);
        writeToOutbox(KafkaTopic.INVENTORY_RESERVE_FAIL, String.valueOf(orderId), payload);
    }

    public void publishReleased(Long orderId, String sku, int quantity) {
        Map<String, Object> payload = buildPayload(
                "orderId", orderId, "sku", sku, "quantity", quantity);
        writeToOutbox(KafkaTopic.INVENTORY_RELEASED, String.valueOf(orderId), payload);
    }

    public void publishLowStock(String sku, int currentStock, int threshold) {
        Map<String, Object> payload = buildPayload(
                "sku", sku, "currentStock", currentStock, "threshold", threshold);
        writeToOutbox(KafkaTopic.INVENTORY_LOW_STOCK, sku, payload);
    }

    public void publishOutOfStock(String sku, Long productVariantId) {
        Map<String, Object> payload = buildPayload(
                "sku", sku, "productVariantId", productVariantId);
        writeToOutbox(KafkaTopic.INVENTORY_OUT_OF_STOCK, sku, payload);
    }

    private void writeToOutbox(String topic, String key, Map<String, Object> payload) {
        try {
            String eventId = UUID.randomUUID().toString();
            payload.put("eventId",    eventId);
            payload.put("occurredAt", Instant.now().toString());

            OutboxEvent event = OutboxEvent.builder()
                    .eventId(eventId)
                    .topic(topic)
                    .messageKey(key)
                    .payload(objectMapper.writeValueAsString(payload))
                    .status(OutboxEvent.OutboxStatus.PENDING)
                    .build();

            outboxRepository.save(event);
            log.debug("Event written to outbox: topic={} eventId={}", topic, eventId);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize outbox event for topic [{}]: {}", topic, e.getMessage());
            throw new RuntimeException("Event serialization failed", e);
        }
    }

    private Map<String, Object> buildPayload(Object... keysAndValues) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < keysAndValues.length - 1; i += 2) {
            map.put(String.valueOf(keysAndValues[i]), keysAndValues[i + 1]);
        }
        return map;
    }
}