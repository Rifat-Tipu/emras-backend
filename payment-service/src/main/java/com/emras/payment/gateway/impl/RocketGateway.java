package com.emras.payment.gateway.impl;

import com.emras.payment.entity.Payment;
import com.emras.payment.gateway.GatewayResult;
import com.emras.payment.gateway.PaymentGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Rocket (Dutch-Bangla Bank) gateway implementation.
 *
 * Simulation rule: always success.
 * Production: would call DBBL Rocket API.
 */
@Slf4j
@Component
public class RocketGateway implements PaymentGateway {

    @Override
    public Payment.PaymentMethod supports() {
        return Payment.PaymentMethod.ROCKET;
    }

    @Override
    public GatewayResult process(Long orderId, BigDecimal amount) {
        String txId = "RKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("Rocket payment success: orderId={} txId={}", orderId, txId);
        return GatewayResult.success(txId,
                "{\"method\":\"Rocket\",\"status\":\"SUCCESS\",\"TxnID\":\"" + txId + "\"}");
    }
}