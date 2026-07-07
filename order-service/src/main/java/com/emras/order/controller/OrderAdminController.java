package com.emras.order.controller;
import com.emras.order.constant.ApiEndpointConstant;
import com.emras.order.constant.ErrorMessages;
import com.emras.order.constant.SuccessMessages;
import com.emras.order.dto.request.UpdateOrderStatusRequest;
import com.emras.order.dto.response.OrderResponse;
import com.emras.order.exception.OrderException;
import com.emras.order.model.ApiResponse;
import com.emras.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin — Orders", description = "Order management for admins")
public class OrderAdminController {
    private final OrderService orderService;
    private void requireAdmin(String userRole) {
        if (userRole == null ||
                !Arrays.asList(userRole.split(",")).contains("ROLE_ADMIN")) {
            throw new OrderException(ErrorMessages.ACCESS_DENIED, "ACCESS_DENIED");
        }
    }
    @PatchMapping(ApiEndpointConstant.ADMIN_ORDER_STATUS)
    @Operation(summary = "Update order status (Admin only)")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        requireAdmin(userRole);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.STATUS_UPDATED,
                orderService.updateOrderStatus(id, request),
                HttpStatus.OK));
    }
}