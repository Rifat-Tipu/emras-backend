package com.emras.api_gateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * Binds all downstream service URLs from application.properties.
 *
 * Each environment (local, dev, prod) defines its own values:
 *   local → http://localhost:8081  (IntelliJ running on host machine)
 *   dev   → http://auth-service:8081  (Docker container name)
 *   prod  → ${AUTH_SERVICE_URL}  (Kubernetes service DNS or env var)
 *
 * Never hardcode URLs in Java. Change URLs by changing properties only.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gateway.services")
public class ServiceUrlProperties {
    private String authServiceUrl;
    private String userServiceUrl;
    private String productServiceUrl;
    private String inventoryServiceUrl;
    private String orderServiceUrl;
    private String paymentServiceUrl;
    private String notificationServiceUrl;
    private String deliveryServiceUrl;
    private String aiAssistantServiceUrl;
    private String reviewServiceUrl;
    private String analyticsServiceUrl;
    private String discountServiceUrl;
}