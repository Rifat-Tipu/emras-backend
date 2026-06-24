package com.emras.inventory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;
/**
 * Emras Inventory Service — port 8084
 *
 * Responsibilities:
 *  - Track stock quantity per SKU (Stock Keeping Unit)
 *  - Reserve stock when an order is placed (prevents overselling)
 *  - Release reservation if payment fails or order is cancelled
 *  - Deduct stock when payment succeeds
 *  - Alert when stock drops below low-stock threshold
 *  - Maintain full audit log of every stock movement
 *
 * Key design:
 *  - Optimistic locking (@Version) on InventoryItem prevents two concurrent
 *    orders from both seeing "1 item in stock" and both succeeding.
 *    One wins, the other gets an ObjectOptimisticLockingFailureException
 *    which we catch and convert to a reservation failure event.
 *
 * Kafka:
 *  - Consumes: order.created, order.cancelled, product.updated
 *  - Publishes: inventory.reserved, inventory.reservation.failed,
 *               inventory.released, inventory.low-stock, inventory.out-of-stock
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
@EnableKafka
@EnableScheduling
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}
}