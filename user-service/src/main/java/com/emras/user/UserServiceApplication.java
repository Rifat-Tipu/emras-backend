package com.emras.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Emras User Service — port 8082
 *
 * Responsibilities:
 *  - Customer profile management (name, phone, avatar, language preference)
 *  - Address management with Bangladesh-specific fields (division, district, thana)
 *  - Wishlist management
 *  - Notification preferences
 *
 * Key behaviour:
 *  - Listens to "user.registered" Kafka event from Auth Service
 *    and automatically creates a UserProfile for each new user.
 *  - Never validates JWT — trusts X-User-Id header set by the API Gateway.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
@EnableKafka
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}
}