package com.emras.order.kafka.consumer;

import com.emras.order.constant.KafkaTopic;
import com.emras.order.entity.Order;
import com.emras.order.entity.ProcessedEvent;
import com.emras.order.exception.OrderNotFoundException;
import com.emras.order.kafka.producer.OrderEventProducer;
import com.emras.order.repository.OrderRepository;
import com.emras.order.repository.ProcessedEventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryEventConsumer {

    private static final String CONSUMER_GROUP = "order-service-group";

    private final OrderRepository          orderRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final OrderEventProducer       eventProducer;
    private final ObjectMapper             objectMapper;

    @KafkaListener(
            topics = KafkaTopic.INVENTORY_RESERVED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void onInventoryReserved(String message, Acknowledgment acknowledgment) {
        try {
            JsonNode payload = objectMapper.readTree(message);
            String eventId   = extractEventId(payload);

            if (isDuplicate(eventId)) {
                acknowledgment.acknowledge();
                return;
            }

            Long orderId = payload.get("orderId").asLong();
            log.info("Inventory reserved for orderId={}", orderId);

            Order order = orderRepository.findByIdWithItems(orderId)
                    .orElseThrow(OrderNotFoundException::new);
            orderRepository.findByIdWithHistory(orderId);

            if (order.getStatus() != Order.OrderStatus.PENDING) {
                log.warn("Order {} is not PENDING ({}), skipping", orderId, order.getStatus());
                markProcessed(eventId);
                acknowledgment.acknowledge();
                return;
            }

            order.addStatusHistory(Order.OrderStatus.CONFIRMED,
                    "Stock reserved by Inventory Service");
            orderRepository.save(order);

            eventProducer.publishOrderConfirmed(order);
            markProcessed(eventId);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process inventory.reserved: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = KafkaTopic.INVENTORY_RESERVE_FAIL,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void onInventoryReservationFailed(String message, Acknowledgment acknowledgment) {
        try {
            JsonNode payload = objectMapper.readTree(message);
            String eventId   = extractEventId(payload);

            if (isDuplicate(eventId)) {
                acknowledgment.acknowledge();
                return;
            }

            Long   orderId = payload.get("orderId").asLong();
            String reason  = payload.path("reason").asText("Insufficient stock");

            log.warn("Inventory reservation failed for orderId={} reason={}", orderId, reason);

            Order order = orderRepository.findByIdWithItems(orderId)
                    .orElseThrow(OrderNotFoundException::new);
            orderRepository.findByIdWithHistory(orderId);

            order.addStatusHistory(Order.OrderStatus.CANCELLED,
                    "Cancelled: " + reason);
            orderRepository.save(order);

            order.getItems().forEach(item ->
                    eventProducer.publishOrderCancelled(order, item, reason));

            markProcessed(eventId);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process inventory.reservation.failed: {}", e.getMessage(), e);
        }
    }

    private String extractEventId(JsonNode payload) {
        if (!payload.has("eventId") || payload.get("eventId").isNull()) {
            return "fallback-" + payload.path("orderId").asText()
                    + "-" + System.currentTimeMillis();
        }
        return payload.get("eventId").asText();
    }

    private boolean isDuplicate(String eventId) {
        return processedEventRepository
                .existsByEventIdAndConsumerGroup(eventId, CONSUMER_GROUP);
    }

    private void markProcessed(String eventId) {
        processedEventRepository.save(ProcessedEvent.builder()
                .eventId(eventId)
                .consumerGroup(CONSUMER_GROUP)
                .build());
    }
}