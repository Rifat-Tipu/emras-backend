package com.emras.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

// Modern Jackson 3 imports
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RedisConfig {

    /**
     * Primary template — String keys and String values.
     * This is what AuthServiceImpl uses everywhere.
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    /**
     * Generic template — String keys and JSON-serialized Object values.
     * Available for future use if we need to cache complex objects.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Keys are plain strings — human-readable in Redis CLI
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // FIXED: Using Jackson 3's immutable Builder API.
        // JavaTimeModule is auto-registered, and dates are strings by default!
        ObjectMapper objectMapper = JsonMapper.builder().build();

        // JacksonJsonRedisSerializer now resolves perfectly with this mapper
        JacksonJsonRedisSerializer<Object> jsonSerializer =
                new JacksonJsonRedisSerializer<>(objectMapper, Object.class);

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();

        return template;
    }
}