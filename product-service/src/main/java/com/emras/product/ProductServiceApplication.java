package com.emras.product;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.annotation.EnableKafka;
/**
 * Emras Product Service — port 8083
 *
 * Responsibilities:
 *  - Product catalog management (CRUD)
 *  - Category management (hierarchical: Men > Shirts > Formal)
 *  - Product variants (size × color = SKU)
 *  - Product images (stored in MinIO, URL saved here)
 *  - Redis caching for product details and listings
 *  - Publishes "product.updated" Kafka event on every change
 *  - Supports both English and Bangla names/descriptions
 *
 * Security model:
 *  - Public endpoints: GET /api/v1/products/**, GET /api/v1/categories/**
 *  - Admin endpoints: everything under /api/v1/admin/**
 *  - No Spring Security here — Gateway validates JWT and adds X-User-Role header
 *  - Admin check is done by reading X-User-Role header in controllers
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
@EnableKafka
@EnableCaching
public class ProductServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}
}