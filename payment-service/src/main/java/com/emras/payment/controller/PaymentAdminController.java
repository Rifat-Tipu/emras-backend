package com.emras.payment.controller;

import com.emras.payment.constant.ApiEndpointConstant;
import com.emras.payment.constant.ErrorMessages;
import com.emras.payment.constant.SuccessMessages;
import com.emras.payment.dto.response.PaymentResponse;
import com.emras.payment.exception.PaymentException;
import com.emras.payment.model.ApiResponse;
import com.emras.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin — Payments", description = "Payment management for admins")
public class PaymentAdminController {

    private final PaymentService paymentService;

    private void requireAdmin(String userRole) {
        if (userRole == null ||
                !Arrays.asList(userRole.split(",")).contains("ROLE_ADMIN")) {
            throw new PaymentException(ErrorMessages.ACCESS_DENIED, "ACCESS_DENIED");
        }
    }

    @PostMapping(ApiEndpointConstant.ADMIN_REFUND)
    @Operation(summary = "Initiate refund for a payment (Admin only)")
    public ResponseEntity<ApiResponse<PaymentResponse>> refund(
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long id,
            @RequestParam(required = false) String note) {

        requireAdmin(userRole);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.REFUND_INITIATED,
                paymentService.initiateRefund(id, note),
                HttpStatus.OK));
    }
}