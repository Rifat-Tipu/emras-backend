package com.emras.order.controller;
import com.emras.order.constant.ApiEndpointConstant;
import com.emras.order.constant.SuccessMessages;
import com.emras.order.dto.request.CreateOrderRequest;
import com.emras.order.dto.response.OrderResponse;
import com.emras.order.dto.response.PagedResponse;
import com.emras.order.model.ApiResponse;
import com.emras.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order placement and tracking")
public class OrderController {
    private final OrderService orderService;
    @PostMapping(ApiEndpointConstant.ORDERS)
    @Operation(summary = "Place a new order")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateOrderRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                SuccessMessages.ORDER_PLACED,
                orderService.createOrder(userId, request),
                HttpStatus.CREATED));
    }
    @GetMapping(ApiEndpointConstant.ORDER_BY_ID)
    @Operation(summary = "Get order details by ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.ORDER_FETCHED,
                orderService.getOrderById(userId, id),
                HttpStatus.OK));
    }
    @GetMapping(ApiEndpointConstant.ORDER_HISTORY)
    @Operation(summary = "Get order history for authenticated user")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getOrderHistory(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.ORDERS_FETCHED,
                orderService.getOrderHistory(userId,
                        PageRequest.of(page, size, Sort.by("createdAt").descending())),
                HttpStatus.OK));
    }
    @PatchMapping(ApiEndpointConstant.ORDER_CANCEL)
    @Operation(summary = "Cancel an order")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.ORDER_CANCELLED,
                orderService.cancelOrder(userId, id),
                HttpStatus.OK));
    }
}