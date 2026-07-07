package com.emras.order.client;
import com.emras.order.client.dto.AddressResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.List;

@FeignClient(
        name = "user-service",
        url  = "${feign.clients.user-service-url}"
)
public interface UserClient {

    /**
     * Jackson cannot deserialize ApiResponse<T> generics through Feign.
     * We use concrete inner wrapper classes here instead.
     */
    @GetMapping("/api/v1/users/addresses")
    AddressListWrapper getUserAddresses(
            @RequestHeader("X-User-Id") Long userId);

    @GetMapping("/api/v1/users/addresses/{addressId}")
    AddressWrapper getAddressById(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long addressId);

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AddressWrapper(
            @JsonProperty("data") AddressResponse data
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AddressListWrapper(
            @JsonProperty("data") List<AddressResponse> data
    ) {}
}