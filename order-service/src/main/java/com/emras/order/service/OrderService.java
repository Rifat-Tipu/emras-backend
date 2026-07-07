package com.emras.order.service;
import com.emras.order.dto.request.CreateOrderRequest;
import com.emras.order.dto.request.UpdateOrderStatusRequest;
import com.emras.order.dto.response.OrderResponse;
import com.emras.order.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse createOrder(Long userId, CreateOrderRequest request);
    OrderResponse getOrderById(Long userId, Long orderId);
    PagedResponse<OrderResponse> getOrderHistory(Long userId, Pageable pageable);
    OrderResponse cancelOrder(Long userId, Long orderId);
    OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);
}