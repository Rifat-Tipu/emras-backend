package com.emras.auth.config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
/**
 * Redis configuration for the Auth Service.
 *
 * Two templates are registered:
 *
 *  StringRedisTemplate  — for simple String key/value pairs.
 *                         Used for: OTP codes, refresh token whitelist,
 *                         password reset tokens, login attempt counters.
 *
 *  RedisTemplate<String, Object> — for JSON-serialized objects.
 *                         Available if we need to cache complex objects later.
 *
 * Both use StringRedisSerializer for keys so keys are human-readable
 * in Redis CLI / RedisInsight (no byte-prefix garbage).
 */
@Configuration
public class RedisConfig {
    /**
     * Primary template for Auth Service — String keys and String values.
     * Injected via: @Autowired StringRedisTemplate redisTemplate
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
    /**
     * Generic template for JSON-serialized objects.
     * Injected via: @Autowired RedisTemplate<String, Object> redisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();

        return template;
    }
}