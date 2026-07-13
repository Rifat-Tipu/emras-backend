package com.emras.payment.kafka.producer;

import com.emras.payment.constant.KafkaTopic;
import com.emras.payment.entity.OutboxEvent;
import com.emras.payment.entity.Payment;
import com.emras.payment.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper          objectMapper;

    public void publishPaymentSuccess(Payment payment) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId",       payment.getOrderId());
        payload.put("userId",        payment.getUserId());
        payload.put("paymentId",     payment.getId());
        payload.put("amount",        payment.getAmount());
        payload.put("method",        payment.getMethod().name());
        payload.put("transactionId", payment.getTransactionId());
        writeToOutbox(KafkaTopic.PAYMENT_SUCCESS,
                String.valueOf(payment.getOrderId()), payload);
    }

    public void publishPaymentFailed(Payment payment) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId",      payment.getOrderId());
        payload.put("userId",       payment.getUserId());
        payload.put("paymentId",    payment.getId());
        payload.put("amount",       payment.getAmount());
        payload.put("method",       payment.getMethod().name());
        payload.put("reason",       payment.getFailureReason());
        writeToOutbox(KafkaTopic.PAYMENT_FAILED,
                String.valueOf(payment.getOrderId()), payload);
    }

    public void publishPaymentRefunded(Payment payment) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId",      payment.getOrderId());
        payload.put("userId",       payment.getUserId());
        payload.put("paymentId",    payment.getId());
        payload.put("refundAmount", payment.getRefundAmount());
        writeToOutbox(KafkaTopic.PAYMENT_REFUNDED,
                String.valueOf(payment.getOrderId()), payload);
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

            log.debug("Payment event written to outbox: topic={} eventId={}",
                    topic, eventId);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize payment event: {}", e.getMessage());
            throw new RuntimeException("Event serialization failed", e);
        }
    }
}