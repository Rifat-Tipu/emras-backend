package com.emras.payment.gateway.impl;

import com.emras.payment.entity.Payment;
import com.emras.payment.gateway.GatewayResult;
import com.emras.payment.gateway.PaymentGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Nagad payment gateway implementation.
 *
 * Simulation rule: amount > 500 BDT = success.
 * Production: would call Nagad Open API.
 * Docs: https://nagad.com.bd/developer/
 */
@Slf4j
@Component
public class NagadGateway implements PaymentGateway {

    @Override
    public Payment.PaymentMethod supports() {
        return Payment.PaymentMethod.NAGAD;
    }

    @Override
    public GatewayResult process(Long orderId, BigDecimal amount) {
        String txId = "NGD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        boolean success = amount.compareTo(BigDecimal.valueOf(500)) > 0;

        if (success) {
            log.info("Nagad payment success: orderId={} txId={}", orderId, txId);
            return GatewayResult.success(txId,
                    "{\"method\":\"Nagad\",\"status\":\"Success\",\"merchantOrderId\":\"" + orderId + "\"}");
        }

        log.warn("Nagad payment failed: orderId={} amount={}", orderId, amount);
        return GatewayResult.failure(
                "{\"method\":\"Nagad\",\"status\":\"Failed\"}",
                "Nagad minimum transaction amount is 500 BDT");
    }
}