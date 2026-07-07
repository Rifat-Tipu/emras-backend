package com.emras.order.service.impl;

import com.emras.order.client.ProductClient;
import com.emras.order.client.UserClient;
import com.emras.order.client.dto.AddressResponse;
import com.emras.order.client.dto.ProductResponse;
import com.emras.order.constant.ErrorMessages;
import com.emras.order.dto.request.CreateOrderRequest;
import com.emras.order.dto.request.OrderItemRequest;
import com.emras.order.dto.request.UpdateOrderStatusRequest;
import com.emras.order.dto.response.OrderResponse;
import com.emras.order.dto.response.PagedResponse;
import com.emras.order.entity.Order;
import com.emras.order.entity.OrderItem;
import com.emras.order.exception.OrderException;
import com.emras.order.exception.OrderNotFoundException;
import com.emras.order.kafka.producer.OrderEventProducer;
import com.emras.order.mapper.OrderMapper;
import com.emras.order.repository.OrderRepository;
import com.emras.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository    orderRepository;
    private final OrderMapper        orderMapper;
    private final OrderEventProducer eventProducer;
    private final ProductClient      productClient;
    private final UserClient         userClient;

    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {

        // ── Step 1: Fetch delivery address via FeignClient ────────────────
        AddressResponse address = userClient
                .getAddressById(userId, request.addressId())
                .data();

        if (address == null) {
            throw new OrderException(ErrorMessages.ADDRESS_NOT_FOUND, "ADDRESS_NOT_FOUND");
        }

        // ── Step 2: Build order items with price snapshots ────────────────
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount     = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.items()) {

            ProductResponse product = productClient
                    .getProductById(itemReq.productId())
                    .data();

            if (product == null || !"ACTIVE".equals(product.status())) {
                throw new OrderException(
                        ErrorMessages.PRODUCT_NOT_AVAILABLE + ": " + itemReq.productId(),
                        "PRODUCT_NOT_AVAILABLE");
            }

            BigDecimal effectivePrice = product.discountPrice() != null
                    && product.discountPrice().compareTo(BigDecimal.ZERO) > 0
                    ? product.discountPrice()
                    : product.price();

            BigDecimal subtotal = effectivePrice.multiply(
                    BigDecimal.valueOf(itemReq.quantity()));

            OrderItem orderItem = OrderItem.builder()
                    .productId(itemReq.productId())
                    .productVariantId(itemReq.productVariantId())
                    .sku(itemReq.sku())
                    .productName(product.nameEn())
                    .unitPrice(product.price())
                    .discountPrice(product.discountPrice() != null
                            ? product.discountPrice() : BigDecimal.ZERO)
                    .quantity(itemReq.quantity())
                    .subtotal(subtotal)
                    .build();

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(subtotal);
        }

        // ── Step 3: Create the order ──────────────────────────────────────
        Order order = Order.builder()
                .userId(userId)
                .status(Order.OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .discountAmount(BigDecimal.ZERO)
                .deliveryCharge(BigDecimal.ZERO)
                .couponCode(request.couponCode())
                .paymentMethod(request.paymentMethod())
                .deliveryAddress(address.toFormattedString())
                .addressId(request.addressId())
                .notes(request.notes())
                .build();

        order.addStatusHistory(Order.OrderStatus.PENDING, "Order placed by customer");

        orderItems.forEach(item -> item.setOrder(order));
        order.getItems().addAll(orderItems);

        Order saved = orderRepository.save(order);

        // ── Step 4: Publish order.created to Kafka via Outbox ─────────────
        saved.getItems().forEach(item ->
                eventProducer.publishOrderCreated(saved, item));

        log.info("Order created: id={} userId={} total={} items={}",
                saved.getId(), userId, totalAmount, saved.getItems().size());

        return orderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserIdWithItems(orderId, userId)
                .orElseThrow(OrderNotFoundException::new);
        orderRepository.findByIdWithHistory(orderId);
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getOrderHistory(Long userId, Pageable pageable) {
        Page<Order> page = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return new PagedResponse<>(
                orderMapper.toResponseList(page.getContent()),
                page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserIdWithItems(orderId, userId)
                .orElseThrow(OrderNotFoundException::new);
        orderRepository.findByIdWithHistory(orderId);

        if (order.getStatus() != Order.OrderStatus.PENDING
                && order.getStatus() != Order.OrderStatus.CONFIRMED) {
            throw new OrderException(ErrorMessages.ORDER_CANCEL_DENIED, "CANCEL_DENIED");
        }

        order.addStatusHistory(Order.OrderStatus.CANCELLED, "Cancelled by customer");
        orderRepository.save(order);

        order.getItems().forEach(item ->
                eventProducer.publishOrderCancelled(order, item, "Cancelled by customer"));

        log.info("Order {} cancelled by userId={}", orderId, userId);
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(OrderNotFoundException::new);
        orderRepository.findByIdWithHistory(orderId);

        order.addStatusHistory(request.status(),
                request.note() != null ? request.note() : "Status updated by admin");
        orderRepository.save(order);

        return orderMapper.toResponse(order);
    }
}