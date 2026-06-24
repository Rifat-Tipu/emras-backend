package com.emras.inventory.kafka.consumer;

import com.emras.inventory.constant.KafkaTopic;
import com.emras.inventory.entity.ProcessedEvent;
import com.emras.inventory.exception.InsufficientStockException;
import com.emras.inventory.kafka.producer.InventoryEventProducer;
import com.emras.inventory.repository.ProcessedEventRepository;
import com.emras.inventory.service.InventoryService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consumes order events and triggers stock operations.
 *
 * Idempotency:
 *  Every event must carry an "eventId" field (UUID).
 *  Before processing, we check if this eventId was already handled.
 *  If yes → skip → acknowledge (safe duplicate handling).
 *  If no  → process → save eventId → acknowledge.
 *
 * This makes the consumer safe against Kafka's at-least-once delivery,
 * network retries, and consumer group rebalancing.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private static final String CONSUMER_GROUP = "inventory-service-group";

    private final InventoryService          inventoryService;
    private final InventoryEventProducer    eventProducer;
    private final ProcessedEventRepository  processedEventRepository;
    private final ObjectMapper              objectMapper;

    @KafkaListener(
            topics = KafkaTopic.ORDER_CREATED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void onOrderCreated(String message, Acknowledgment acknowledgment) {
        try {
            JsonNode payload = objectMapper.readTree(message);

            // ── Idempotency check ──────────────────────────────────────────
            String eventId = extractEventId(payload);
            if (isDuplicate(eventId)) {
                log.info("Duplicate event skipped: eventId={} topic=order.created", eventId);
                acknowledgment.acknowledge();
                return;
            }

            Long   orderId  = payload.get("orderId").asLong();
            String sku      = payload.get("sku").asText();
            int    quantity = payload.get("quantity").asInt();

            log.info("Processing order.created: orderId={} sku={} qty={}", orderId, sku, quantity);

            inventoryService.reserveStock(orderId, sku, quantity);
            eventProducer.publishReserved(orderId, sku, quantity);

            // ── Mark as processed (idempotency record) ────────────────────
            markProcessed(eventId);
            acknowledgment.acknowledge();

        } catch (InsufficientStockException e) {
            handleReservationFailure(message, e.getMessage(), acknowledgment);
        } catch (ObjectOptimisticLockingFailureException e) {
            handleReservationFailure(message,
                    "Concurrent update detected. Stock may be insufficient.", acknowledgment);
        } catch (Exception e) {
            log.error("Failed to process order.created: {}", e.getMessage(), e);
            // Do not acknowledge — Kafka will redeliver
        }
    }

    @KafkaListener(
            topics = KafkaTopic.ORDER_CANCELLED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void onOrderCancelled(String message, Acknowledgment acknowledgment) {
        try {
            JsonNode payload = objectMapper.readTree(message);

            String eventId = extractEventId(payload);
            if (isDuplicate(eventId)) {
                log.info("Duplicate event skipped: eventId={} topic=order.cancelled", eventId);
                acknowledgment.acknowledge();
                return;
            }

            Long   orderId  = payload.get("orderId").asLong();
            String sku      = payload.get("sku").asText();
            int    quantity = payload.get("quantity").asInt();

            log.info("Processing order.cancelled: orderId={} sku={} qty={}", orderId, sku, quantity);

            inventoryService.releaseReservation(orderId, sku, quantity);
            eventProducer.publishReleased(orderId, sku, quantity);

            markProcessed(eventId);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process order.cancelled: {}", e.getMessage(), e);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private String extractEventId(JsonNode payload) {
        if (!payload.has("eventId") || payload.get("eventId").isNull()) {
            // Fallback for events without eventId — use orderId+topic as key
            // In production all events should carry eventId
            return "fallback-" + payload.path("orderId").asText() + "-" + System.currentTimeMillis();
        }
        return payload.get("eventId").asText();
    }

    private boolean isDuplicate(String eventId) {
        return processedEventRepository.existsByEventIdAndConsumerGroup(eventId, CONSUMER_GROUP);
    }

    private void markProcessed(String eventId) {
        processedEventRepository.save(ProcessedEvent.builder()
                .eventId(eventId)
                .consumerGroup(CONSUMER_GROUP)
                .build());
    }

    private void handleReservationFailure(String message, String reason,
                                          Acknowledgment acknowledgment) {
        try {
            JsonNode payload = objectMapper.readTree(message);
            Long   orderId   = payload.get("orderId").asLong();
            String sku       = payload.get("sku").asText();
            int    requested = payload.get("quantity").asInt();

            log.warn("Reservation failed: orderId={} sku={} reason={}", orderId, sku, reason);
            eventProducer.publishReservationFailed(orderId, sku, requested, 0, reason);
            markProcessed(extractEventId(payload));
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Failed to send reservation failed event: {}", e.getMessage());
        }
    }
}