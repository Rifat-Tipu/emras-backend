package com.emras.product.kafka.producer;
import com.emras.product.constant.KafkaTopic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Publishes product domain events to Kafka.
 *
 * Consumers:
 *  product.updated → Inventory Service (sync new SKUs), Analytics Service
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    public void publishProductUpdated(Long productId, String nameEn, String slug,
                                      String status) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("productId",  productId);
        payload.put("nameEn",     nameEn);
        payload.put("slug",       slug);
        payload.put("status",     status);
        payload.put("occurredAt", Instant.now().toString());
        payload.put("traceId",    MDC.get("traceId"));

        publish(KafkaTopic.PRODUCT_UPDATED, String.valueOf(productId), payload);
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
                    log.debug("Published to [{}] offset=[{}]",
                            topic, result.getRecordMetadata().offset());
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Serialization failed for topic [{}]: {}", topic, e.getMessage());
        }
    }
}