package com.emras.auth.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;
/**
 * Supplies the current username to JPA's @CreatedBy and @LastModifiedBy
 * fields in BaseEntity.
 *
 * During a normal request:  returns the authenticated user's email.
 * During system operations: returns "system" (e.g. Flyway migration, scheduler).
 * When unauthenticated:     returns "anonymous" (e.g. registration endpoint).
 */
@Configuration
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()
                    || "anonymousUser".equals(auth.getPrincipal())) {
                return Optional.of("anonymous");
            }
            return Optional.of(auth.getName());
        };
    }
}