package com.emras.payment.service;

import com.emras.payment.dto.response.PaymentResponse;

import java.util.List;

public interface PaymentService {
    List<PaymentResponse> getPaymentsByOrder(Long orderId);
    PaymentResponse getPaymentById(Long id);
    PaymentResponse initiateRefund(Long paymentId, String adminNote);
}