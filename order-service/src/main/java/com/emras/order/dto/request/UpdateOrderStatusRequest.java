package com.emras.order.dto.request;
import com.emras.order.entity.Order;
import jakarta.validation.constraints.NotNull;
public record UpdateOrderStatusRequest(
        @NotNull(message = "Status is required.")
        Order.OrderStatus status,
        String note
) {}