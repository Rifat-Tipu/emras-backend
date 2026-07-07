package com.emras.order.dto.request;
import com.emras.order.entity.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
public record CreateOrderRequest(
        @NotNull(message = "Payment method is required.")
        Order.PaymentMethod paymentMethod,
        @NotNull(message = "Delivery address ID is required.")
        Long addressId,
        String couponCode,
        String notes,
        @NotEmpty(message = "Order must contain at least one item.")
        @Valid
        List<OrderItemRequest> items
) {}