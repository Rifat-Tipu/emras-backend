package com.emras.user.kafka.consumer;
import com.emras.user.constant.KafkaTopic;
import com.emras.user.service.UserProfileService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
/**
 * Listens to "user.registered" event from Auth Service.
 * Automatically creates a UserProfile for every new registration.
 *
 * This is the Kafka-driven loose coupling in action:
 *  - Auth Service does NOT call User Service directly.
 *  - Auth Service publishes an event.
 *  - User Service reacts to it independently.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventConsumer {
    private final UserProfileService profileService;
    private final ObjectMapper       objectMapper;
    @KafkaListener(
            topics = KafkaTopic.USER_REGISTERED,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onUserRegistered(String message, Acknowledgment acknowledgment) {
        try {
            JsonNode payload = objectMapper.readTree(message);
            Long   userId = payload.get("userId").asLong();
            String email  = payload.get("email").asText();
            String phone  = payload.has("phone") && !payload.get("phone").isNull()
                    ? payload.get("phone").asText()
                    : null;
            log.info("Received user.registered event — userId={} email={}", userId, email);

            profileService.createProfileFromEvent(userId, email, phone);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process user.registered event: {}", e.getMessage(), e);
            // Do NOT acknowledge — message will be retried
        }
    }
}