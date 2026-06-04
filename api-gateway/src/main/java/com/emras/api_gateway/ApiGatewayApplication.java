package com.emras.api_gateway;
import com.emras.api_gateway.config.ServiceUrlProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
/**
 * Emras API Gateway
 *
 * Single entry point for all microservices.
 * Runs on port 8080.
 *
 * Responsibilities:
 *  - JWT validation on every protected request
 *  - Route requests to the correct microservice
 *  - Add X-User-Id and X-User-Role headers to downstream requests
 *  - CORS configuration for the React frontend
 *  - Rate limiting (future)
 *
 * NOTE: Gateway uses Spring WebFlux internally.
 * This is the one unavoidable exception to the no-reactive rule.
 * No other service uses WebFlux.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties(ServiceUrlProperties.class)
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}
}