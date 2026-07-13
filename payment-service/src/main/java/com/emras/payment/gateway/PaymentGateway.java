package com.emras.payment.gateway;

import com.emras.payment.entity.Payment;

import java.math.BigDecimal;

/**
 * Strategy interface for payment gateway implementations.
 *
 * Each gateway (bKash, Nagad, Rocket, etc.) implements this interface.
 * Adding a new gateway = create a new class implementing this interface.
 * No existing code needs to change — Open/Closed Principle satisfied.
 */
public interface PaymentGateway {

    /**
     * Which payment method this gateway handles.
     */
    Payment.PaymentMethod supports();

    /**
     * Process the payment and return the result.
     *
     * @param orderId  the order being paid for
     * @param amount   amount in BDT
     * @return GatewayResult with success/failure details
     */
    GatewayResult process(Long orderId, BigDecimal amount);
}