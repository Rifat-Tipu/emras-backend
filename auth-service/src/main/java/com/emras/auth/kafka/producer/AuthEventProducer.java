package com.emras.auth.kafka.producer;

import com.emras.auth.constant.KafkaTopic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Publishes auth-related domain events to Kafka.
 * Fire-and-forget — main auth flow is never blocked if Kafka is slow.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    /** Published when a new user registers. */
    public void publishUserRegistered(Long userId, String email, String phone) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId",     userId);
        payload.put("email",      email);
        payload.put("phone",      phone);
        payload.put("occurredAt", Instant.now().toString());
        payload.put("traceId",    MDC.get("traceId"));
        publish(KafkaTopic.USER_REGISTERED, String.valueOf(userId), payload);
    }

    /** Published on every successful login. */
    public void publishUserLoggedIn(Long userId, String email, String ipAddress) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId",     userId);
        payload.put("email",      email);
        payload.put("ipAddress",  ipAddress);
        payload.put("occurredAt", Instant.now().toString());
        payload.put("traceId",    MDC.get("traceId"));
        publish(KafkaTopic.USER_LOGGED_IN, String.valueOf(userId), payload);
    }

    /** Published when account is locked after too many failed attempts. */
    public void publishAccountLocked(Long userId, String email) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId",     userId);
        payload.put("email",      email);
        payload.put("occurredAt", Instant.now().toString());
        payload.put("traceId",    MDC.get("traceId"));
        publish(KafkaTopic.ACCOUNT_LOCKED, String.valueOf(userId), payload);
    }

    private void publish(String topic, String key, Map<String, Object> payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(topic, key, json);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish to [{}]: {}", topic, ex.getMessage());
                } else {
                    log.debug("Published to [{}] partition=[{}] offset=[{}]",
                            topic,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Serialization failed for topic [{}]: {}", topic, e.getMessage());
        }
    }
}