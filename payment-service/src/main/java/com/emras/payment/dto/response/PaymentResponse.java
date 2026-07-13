package com.emras.payment.dto.response;

import com.emras.payment.entity.Payment;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long                   id,
        Long                   orderId,
        Long                   userId,
        BigDecimal             amount,
        Payment.PaymentStatus  status,
        Payment.PaymentMethod  method,
        String                 transactionId,
        String                 failureReason,
        Instant                paidAt,
        Instant                refundedAt,
        BigDecimal             refundAmount,
        Instant                createdAt
) {}