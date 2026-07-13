package com.emras.api_gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
/**
 * API Gateway route definitions.
 *
 * Route URLs come from ServiceUrlProperties which reads from
 * the active profile's application.properties — never from Java code.
 *
 * To add a new service:
 *   1. Add its URL property in all three profiles (local, dev, prod).
 *   2. Add a field in ServiceUrlProperties.
 *   3. Add a @Bean here pointing to that field.
 */
@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
    private final ServiceUrlProperties services;

    @Bean
    public RouterFunction<ServerResponse> authServiceRoutes() {
        return GatewayRouterFunctions.route("auth_service_route")
                .route(RequestPredicates.path("/api/v1/auth/**"), HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(services.getAuthServiceUrl()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userServiceRoutes() {
        return GatewayRouterFunctions.route("user_service_route")
                .route(RequestPredicates.path("/api/v1/users/**"), HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(services.getUserServiceUrl()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> productServiceRoutes() {
        return GatewayRouterFunctions.route("product_service_route")
                .route(RequestPredicates.path("/api/v1/products/**")
                                .or(RequestPredicates.path("/api/v1/categories/**"))
                                .or(RequestPredicates.path("/api/v1/admin/products/**"))
                                .or(RequestPredicates.path("/api/v1/admin/categories/**")),
                        HandlerFunctions.http())
                .before(BeforeFilterFunctions.uri(services.getProductServiceUrl()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoutes() {
        return GatewayRouterFunctions.route("inventory_service_route")
                .route(
                        RequestPredicates.path("/api/v1/inventory/**")
                                .or(RequestPredicates.path("/api/v1/admin/inventory/**")),
                        HandlerFunctions.http()
                )
                .before(BeforeFilterFunctions.uri(services.getInventoryServiceUrl()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoutes() {
        return GatewayRouterFunctions.route("order_service_route")
                .route(
                        RequestPredicates.path("/api/v1/orders/**")
                                .or(RequestPredicates.path("/api/v1/admin/orders/**")),
                        HandlerFunctions.http()
                )
                .before(BeforeFilterFunctions.uri(services.getOrderServiceUrl()))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> paymentServiceRoutes() {
        return GatewayRouterFunctions.route("payment_service_route")
                .route(
                        RequestPredicates.path("/api/v1/payments/**")
                                .or(RequestPredicates.path("/api/v1/admin/payments/**"))
                                .or(RequestPredicates.path("/api/v1/webhooks/**")),
                        HandlerFunctions.http()
                )
                .before(BeforeFilterFunctions.uri(services.getPaymentServiceUrl()))
                .build();
    }
}