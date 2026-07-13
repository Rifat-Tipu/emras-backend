package com.emras.payment.gateway.impl;

import com.emras.payment.entity.Payment;
import com.emras.payment.gateway.GatewayResult;
import com.emras.payment.gateway.PaymentGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * bKash payment gateway implementation.
 *
 * Simulation rule: even amount = success, odd = failure.
 * Production: would call bKash Checkout API.
 * Docs: https://developer.bka.sh/
 */
@Slf4j
@Component
public class BkashGateway implements PaymentGateway {

    @Override
    public Payment.PaymentMethod supports() {
        return Payment.PaymentMethod.BKASH;
    }

    @Override
    public GatewayResult process(Long orderId, BigDecimal amount) {
        String txId = "BKS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        boolean success = amount.toBigInteger()
                .mod(java.math.BigInteger.TWO).intValue() == 0;

        if (success) {
            log.info("bKash payment success: orderId={} txId={}", orderId, txId);
            return GatewayResult.success(txId,
                    "{\"method\":\"bKash\",\"status\":\"SUCCESS\",\"trxID\":\"" + txId + "\"}");
        }

        log.warn("bKash payment failed: orderId={}", orderId);
        return GatewayResult.failure(
                "{\"method\":\"bKash\",\"status\":\"FAILED\"}",
                "bKash transaction declined");
    }
}