package com.emras.payment.gateway;

/**
 * Immutable result returned by any payment gateway.
 *
 * success         = whether payment was accepted
 * transactionId   = gateway-issued transaction ID (null on failure)
 * gatewayResponse = raw JSON from gateway (for audit)
 * failureReason   = human-readable reason (null on success)
 */
public record GatewayResult(
        boolean success,
        String  transactionId,
        String  gatewayResponse,
        String  failureReason
) {
    public static GatewayResult success(String transactionId, String gatewayResponse) {
        return new GatewayResult(true, transactionId, gatewayResponse, null);
    }

    public static GatewayResult failure(String gatewayResponse, String failureReason) {
        return new GatewayResult(false, null, gatewayResponse, failureReason);
    }
}