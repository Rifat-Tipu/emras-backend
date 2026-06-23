package com.emras.inventory.service;
import com.emras.inventory.entity.OutboxEvent;
import com.emras.inventory.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
/**
 * Outbox Publisher — reads PENDING outbox events and publishes them to Kafka.
 *
 * Runs every 5 seconds via @Scheduled.
 *
 * Why this is safe:
 *  - Event was written to DB in the same transaction as the inventory update
 *  - If DB commit failed → event never exists → nothing to publish ✓
 *  - If Kafka fails here → status stays PENDING → retried next cycle ✓
 *  - If we crash mid-publish → event stays PENDING → retried on restart ✓
 *
 * Retry logic:
 *  - Up to 5 retries per event
 *  - After 5 failures → status = FAILED → requires manual intervention
 *  - Failed events are logged and should trigger an alert in production
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxPublisherService {
    private static final int MAX_RETRIES = 5;
    private final OutboxEventRepository         outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Scheduled(fixedDelay = 5000)  // runs every 5 seconds
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pending = outboxRepository.findPendingEvents();
        if (pending.isEmpty()) return;
        log.debug("Publishing {} pending outbox events", pending.size());
        for (OutboxEvent event : pending) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getMessageKey(), event.getPayload())
                        .get();  // synchronous send — ensures we know if it succeeded

                event.setStatus(OutboxEvent.OutboxStatus.PUBLISHED);
                event.setPublishedAt(Instant.now());
                log.debug("Outbox event published: topic={} eventId={}",
                        event.getTopic(), event.getEventId());

            } catch (Exception e) {
                event.setRetryCount(event.getRetryCount() + 1);
                event.setLastAttemptAt(Instant.now());
                event.setErrorMessage(e.getMessage());

                if (event.getRetryCount() >= MAX_RETRIES) {
                    event.setStatus(OutboxEvent.OutboxStatus.FAILED);
                    event.setFailedAt(Instant.now());
                    log.error("Outbox event PERMANENTLY FAILED after {} retries: eventId={} topic={}",
                            MAX_RETRIES, event.getEventId(), event.getTopic());
                } else {
                    log.warn("Outbox event publish failed (attempt {}/{}): eventId={} error={}",
                            event.getRetryCount(), MAX_RETRIES, event.getEventId(), e.getMessage());
                }
            }
            outboxRepository.save(event);
        }
    }
}