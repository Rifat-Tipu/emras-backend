package com.emras.payment.gateway.impl;

import com.emras.payment.entity.Payment;
import com.emras.payment.gateway.GatewayResult;
import com.emras.payment.gateway.PaymentGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Cash on Delivery — no external gateway needed.
 *
 * Always succeeds immediately.
 * Actual cash collection happens at delivery time.
 */
@Slf4j
@Component
public class CodGateway implements PaymentGateway {

    @Override
    public Payment.PaymentMethod supports() {
        return Payment.PaymentMethod.COD;
    }

    @Override
    public GatewayResult process(Long orderId, BigDecimal amount) {
        String txId = "COD-" + orderId + "-" + System.currentTimeMillis();
        log.info("COD payment accepted: orderId={} txId={}", orderId, txId);
        return GatewayResult.success(txId,
                "{\"method\":\"COD\",\"status\":\"ACCEPTED\"}");
    }
}