package com.emras.product.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// Jackson 3 imports (Spring Boot 4.x default)
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.Map;
/**
 * Redis cache configuration for Product Service.
 *
 * Uses JacksonJsonRedisSerializer (Spring Data Redis 4.x) with Jackson 3's
 * immutable ObjectMapper builder API.
 * JavaTimeModule and sane date serialization are auto-registered by default
 * in Jackson 3's JsonMapper — no manual module registration needed.
 *
 * Two cache regions:
 *   products   — TTL 1 hour  (product detail pages)
 *   categories — TTL 6 hours (rarely changes)
 */
@Configuration
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {

        ObjectMapper objectMapper = JsonMapper.builder().build();

        JacksonJsonRedisSerializer<Object> jsonSerializer =
                new JacksonJsonRedisSerializer<>(objectMapper, Object.class);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(jsonSerializer))
                .disableCachingNullValues();

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(Map.of(
                        "products",   defaultConfig.entryTtl(Duration.ofHours(1)),
                        "categories", defaultConfig.entryTtl(Duration.ofHours(6))
                ))
                .build();
    }
}