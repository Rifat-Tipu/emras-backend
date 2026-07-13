package com.emras.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Emras Payment Service — port 8086
 *
 * Responsibilities:
 *  - Consume order.confirmed events from Kafka
 *  - Initiate payment via Bangladesh payment gateways:
 *      bKash, Nagad, Rocket (mobile banking)
 *      SSLCommerz (card/internet banking)
 *      COD (Cash on Delivery — no gateway needed)
 *  - Store payment transaction records
 *  - Publish payment.success or payment.failed events
 *  - Handle payment callbacks/webhooks from gateways
 *
 * In local/dev environment:
 *  - Gateway calls are SIMULATED (no real API calls)
 *  - COD payments auto-succeed
 *  - bKash/Nagad/Rocket simulate success based on amount parity
 *    (even amount = success, odd amount = failure — for testing)
 *
 * Outbox Pattern and Idempotency applied same as other services.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
@EnableKafka
@EnableScheduling
public class PaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}
}