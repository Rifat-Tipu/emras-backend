package com.emras.order;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;
/**
 * Emras Order Service — port 8085
 *
 * Responsibilities:
 *  - Accept order placement requests from customers
 *  - Validate product availability via FeignClient → Product Service
 *  - Fetch delivery address via FeignClient → User Service
 *  - Coordinate the full purchase Saga via Kafka:
 *      order.created → inventory reserves stock
 *      inventory.reserved → order moves to CONFIRMED
 *      payment.success → order moves to COMPLETED
 *      payment.failed / inventory.reservation.failed → order CANCELLED
 *  - Maintain full order status history
 *  - Use Outbox Pattern for guaranteed Kafka event delivery
 *  - Idempotency on all consumed Kafka events
 *
 * Saga steps:
 *  PENDING → CONFIRMED → PROCESSING → COMPLETED
 *                      ↘ CANCELLED (compensating)
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
@EnableKafka
@EnableScheduling
@EnableFeignClients
public class OrderServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}
}