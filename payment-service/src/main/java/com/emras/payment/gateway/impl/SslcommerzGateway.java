package com.emras.payment.gateway.impl;

import com.emras.payment.entity.Payment;
import com.emras.payment.gateway.GatewayResult;
import com.emras.payment.gateway.PaymentGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * SSLCommerz gateway implementation (card/internet banking).
 *
 * Simulation rule: always success.
 * Production: would call SSLCommerz API.
 * Docs: https://developer.sslcommerz.com/
 */
@Slf4j
@Component
public class SslcommerzGateway implements PaymentGateway {

    @Override
    public Payment.PaymentMethod supports() {
        return Payment.PaymentMethod.SSLCOMMERZ;
    }

    @Override
    public GatewayResult process(Long orderId, BigDecimal amount) {
        String txId = "SSL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("SSLCommerz payment success: orderId={} txId={}", orderId, txId);
        return GatewayResult.success(txId,
                "{\"method\":\"SSLCommerz\",\"status\":\"VALID\",\"tran_id\":\"" + txId + "\"}");
    }
}