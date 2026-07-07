package com.emras.order.dto.response;
import com.emras.order.entity.Order;
import java.time.Instant;
public record OrderStatusHistoryResponse(
        Order.OrderStatus status,
        String            note,
        Instant           createdAt
) {}