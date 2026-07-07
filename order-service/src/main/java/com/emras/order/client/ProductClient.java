package com.emras.order.client;
import com.emras.order.client.dto.ProductResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(
        name = "product-service",
        url  = "${feign.clients.product-service-url}"
)
public interface ProductClient {

    @GetMapping("/api/v1/products/{id}")
    ProductWrapper getProductById(@PathVariable Long id);

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ProductWrapper(
            @JsonProperty("data") ProductResponse data
    ) {}
}