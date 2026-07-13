package com.emras.payment.kafka.consumer;

import com.emras.payment.constant.KafkaTopic;
import com.emras.payment.entity.Payment;
import com.emras.payment.entity.ProcessedEvent;
import com.emras.payment.gateway.GatewayResult;
import com.emras.payment.gateway.PaymentGatewayFactory;
import com.emras.payment.kafka.producer.PaymentEventProducer;
import com.emras.payment.repository.PaymentRepository;
import com.emras.payment.repository.ProcessedEventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private static final String CONSUMER_GROUP = "payment-service-group";

    private final PaymentRepository        paymentRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final PaymentEventProducer     eventProducer;
    private final PaymentGatewayFactory    gatewayFactory;
    private final ObjectMapper             objectMapper;

    @KafkaListener(
            topics = KafkaTopic.ORDER_CONFIRMED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void onOrderConfirmed(String message, Acknowledgment acknowledgment) {
        try {
            JsonNode payload = objectMapper.readTree(message);
            String   eventId = extractEventId(payload);

            if (isDuplicate(eventId)) {
                acknowledgment.acknowledge();
                return;
            }

            Long       orderId = payload.get("orderId").asLong();
            Long       userId  = payload.get("userId").asLong();
            BigDecimal amount  = new BigDecimal(payload.get("totalAmount").asText());
            String     method  = payload.get("paymentMethod").asText();

            log.info("Processing payment: orderId={} amount={} method={}",
                    orderId, amount, method);

            if (paymentRepository.existsByOrderIdAndStatus(
                    orderId, Payment.PaymentStatus.SUCCESS)) {
                log.warn("Order {} already paid — skipping", orderId);
                markProcessed(eventId);
                acknowledgment.acknowledge();
                return;
            }

            Payment.PaymentMethod paymentMethod = Payment.PaymentMethod.valueOf(method);

            Payment payment = Payment.builder()
                    .orderId(orderId)
                    .userId(userId)
                    .amount(amount)
                    .method(paymentMethod)
                    .status(Payment.PaymentStatus.PENDING)
                    .build();
            payment = paymentRepository.save(payment);

            // Factory resolves the correct gateway — no switch needed
            GatewayResult result = gatewayFactory
                    .resolve(paymentMethod)
                    .process(orderId, amount);

            if (result.success()) {
                payment.setStatus(Payment.PaymentStatus.SUCCESS);
                payment.setTransactionId(result.transactionId());
                payment.setGatewayResponse(result.gatewayResponse());
                payment.setPaidAt(Instant.now());
                paymentRepository.save(payment);
                eventProducer.publishPaymentSuccess(payment);
                log.info("Payment SUCCESS: orderId={} txId={}", orderId, result.transactionId());
            } else {
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.setFailureReason(result.failureReason());
                payment.setGatewayResponse(result.gatewayResponse());
                paymentRepository.save(payment);
                eventProducer.publishPaymentFailed(payment);
                log.warn("Payment FAILED: orderId={} reason={}", orderId, result.failureReason());
            }

            markProcessed(eventId);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process order.confirmed: {}", e.getMessage(), e);
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
            String   eventId = extractEventId(payload);

            if (isDuplicate(eventId)) {
                acknowledgment.acknowledge();
                return;
            }

            Long orderId = payload.get("orderId").asLong();

            paymentRepository.findTopByOrderIdAndStatusOrderByCreatedAtDesc(
                    orderId, Payment.PaymentStatus.SUCCESS).ifPresent(payment -> {
                payment.setStatus(Payment.PaymentStatus.REFUNDED);
                payment.setRefundAmount(payment.getAmount());
                payment.setRefundedAt(Instant.now());
                paymentRepository.save(payment);
                eventProducer.publishPaymentRefunded(payment);
                log.info("Refund initiated for orderId={}", orderId);
            });

            markProcessed(eventId);
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Failed to process order.cancelled: {}", e.getMessage(), e);
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