package com.emras.product.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Optional;
@Configuration
public class AuditConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            try {
                ServletRequestAttributes attrs =
                        (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                String userId = attrs.getRequest().getHeader("X-User-Id");
                return Optional.ofNullable(userId != null ? "user-" + userId : "system");
            } catch (Exception e) {
                return Optional.of("system");
            }
        };
    }
}