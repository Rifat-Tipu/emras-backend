package com.emras.payment.gateway;

import com.emras.payment.entity.Payment;
import com.emras.payment.exception.PaymentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory that resolves the correct PaymentGateway for a given PaymentMethod.
 *
 * Spring auto-discovers all PaymentGateway implementations via @Component.
 * We inject List<PaymentGateway> — Spring populates it with every bean
 * that implements the interface.
 *
 * To add a new gateway (e.g. Upay):
 *   1. Create UpayGateway implements PaymentGateway
 *   2. Add UPAY to PaymentMethod enum
 *   3. Done — factory picks it up automatically
 *
 * Zero changes to existing code. Open/Closed Principle satisfied.
 */
@Slf4j
@Component
public class PaymentGatewayFactory {

    private final Map<Payment.PaymentMethod, PaymentGateway> gatewayMap;

    /**
     * Spring injects all PaymentGateway beans as a List.
     * We convert to a Map keyed by PaymentMethod for O(1) lookup.
     */
    public PaymentGatewayFactory(List<PaymentGateway> gateways) {
        this.gatewayMap = gateways.stream()
                .collect(Collectors.toMap(
                        PaymentGateway::supports,
                        Function.identity()
                ));

        log.info("Registered {} payment gateways: {}",
                gatewayMap.size(), gatewayMap.keySet());
    }

    /**
     * Resolve the gateway for a payment method.
     *
     * @throws PaymentException if no gateway is registered for the method
     */
    public PaymentGateway resolve(Payment.PaymentMethod method) {
        PaymentGateway gateway = gatewayMap.get(method);

        if (gateway == null) {
            throw new PaymentException(
                    "No payment gateway registered for method: " + method,
                    "GATEWAY_NOT_FOUND");
        }

        return gateway;
    }
}