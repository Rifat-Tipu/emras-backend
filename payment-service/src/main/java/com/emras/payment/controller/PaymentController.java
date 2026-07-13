package com.emras.payment.controller;

import com.emras.payment.constant.ApiEndpointConstant;
import com.emras.payment.constant.SuccessMessages;
import com.emras.payment.dto.response.PaymentResponse;
import com.emras.payment.model.ApiResponse;
import com.emras.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment records and status")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping(ApiEndpointConstant.PAYMENT_BY_ORDER)
    @Operation(summary = "Get all payment attempts for an order")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getByOrder(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.PAYMENTS_FETCHED,
                paymentService.getPaymentsByOrder(orderId),
                HttpStatus.OK));
    }

    @GetMapping(ApiEndpointConstant.PAYMENT_BY_ID)
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<ApiResponse<PaymentResponse>> getById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.PAYMENT_FETCHED,
                paymentService.getPaymentById(id),
                HttpStatus.OK));
    }
}