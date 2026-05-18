package com.emras.auth.config;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import java.util.HashMap;
import java.util.Map;
/**
 * Kafka configuration for the Auth Service.
 *
 * The Auth Service is a PRODUCER only — it does not consume any Kafka events.
 * It publishes:
 *   - user.registered  → consumed by User Service, Notification Service
 *   - user.logged-in   → consumed by Analytics Service
 *   - user.account-locked → consumed by Notification Service
 *
 * Topics are auto-created here (idempotent — safe to run multiple times).
 * In production Kafka clusters, topics are usually pre-created by ops — in
 * that case comment out the @Bean topic declarations below.
 */
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    // ── Producer Factory ──────────────────────────────────────────────────
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Idempotent producer — exactly-once delivery guarantee at the broker level
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);

        return new DefaultKafkaProducerFactory<>(config);
    }
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    // ── Topic Declarations ────────────────────────────────────────────────
    // partitions=1 and replicas=1 are fine for local dev.
    // Increase partitions in production for parallelism.
    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name("user.registered")
                .partitions(1)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic userLoggedInTopic() {
        return TopicBuilder.name("user.logged-in")
                .partitions(1)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic accountLockedTopic() {
        return TopicBuilder.name("user.account-locked")
                .partitions(1)
                .replicas(1)
                .build();
    }
}