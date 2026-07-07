package com.emras.order.service;

import com.emras.order.entity.OutboxEvent;
import com.emras.order.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxPublisherService {

    private static final int MAX_RETRIES = 5;

    private final OutboxEventRepository         outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pending = outboxRepository.findPendingEvents();
        if (pending.isEmpty()) return;

        log.debug("Publishing {} pending order outbox events", pending.size());

        for (OutboxEvent event : pending) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getMessageKey(),
                        event.getPayload()).get();

                event.setStatus(OutboxEvent.OutboxStatus.PUBLISHED);
                event.setPublishedAt(Instant.now());

            } catch (Exception e) {
                event.setRetryCount(event.getRetryCount() + 1);
                event.setLastAttemptAt(Instant.now());
                event.setErrorMessage(e.getMessage());

                if (event.getRetryCount() >= MAX_RETRIES) {
                    event.setStatus(OutboxEvent.OutboxStatus.FAILED);
                    event.setFailedAt(Instant.now());
                    log.error("Order outbox event PERMANENTLY FAILED: eventId={}",
                            event.getEventId());
                }
            }
            outboxRepository.save(event);
        }
    }
}