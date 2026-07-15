package com.emras.notification;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;
/**
 * Emras Notification Service — port 8087
 *
 * Responsibilities:
 *  - Consume events from all services via Kafka
 *  - Send email notifications (via Spring Mail / SMTP)
 *  - Send SMS notifications (simulated locally, real in production)
 *  - Store notification history for audit
 *  - Support EN and BN (Bangla) templates
 *
 * Events consumed:
 *  - user.registered         → welcome email
 *  - order.confirmed         → order confirmation email + SMS
 *  - payment.success         → payment receipt email
 *  - payment.failed          → payment failure alert
 *  - order.cancelled         → cancellation email
 *  - inventory.low-stock     → admin alert email
 *  - inventory.out-of-stock  → admin alert email
 *
 * Local dev: emails logged to console (no real SMTP needed)
 * Production: configure real SMTP (Gmail, SendGrid, etc.)
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
@EnableKafka
@EnableScheduling
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}
}