package com.emras.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Emras Auth Service
 *
 * Responsibilities:
 *  - Email + password registration with email verification
 *  - Phone OTP login (Bangladeshi format: 01x-xxxxxxxx)
 *  - Google and Facebook OAuth2 social login
 *  - JWT access token (15 min) + refresh token (7 days)
 *  - Refresh token rotation via Redis whitelist
 *  - Forgot / reset password
 *  - Account lockout after failed attempts
 *  - Role-based access: CUSTOMER, ADMIN, STAFF, VIEWER
 *
 * Port: 8081
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
@EnableKafka
@EnableScheduling
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}