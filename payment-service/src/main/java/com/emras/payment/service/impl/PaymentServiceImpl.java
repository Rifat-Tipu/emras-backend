package com.emras.payment.service.impl;

import com.emras.payment.dto.response.PaymentResponse;
import com.emras.payment.entity.Payment;
import com.emras.payment.exception.PaymentException;
import com.emras.payment.exception.PaymentNotFoundException;
import com.emras.payment.kafka.producer.PaymentEventProducer;
import com.emras.payment.mapper.PaymentMapper;
import com.emras.payment.repository.PaymentRepository;
import com.emras.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository    paymentRepository;
    private final PaymentMapper        paymentMapper;
    private final PaymentEventProducer eventProducer;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByOrder(Long orderId) {
        return paymentRepository.findByOrderIdOrderByCreatedAtDesc(orderId)
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        return paymentMapper.toResponse(
                paymentRepository.findById(id)
                        .orElseThrow(PaymentNotFoundException::new));
    }

    @Override
    @Transactional
    public PaymentResponse initiateRefund(Long paymentId, String adminNote) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(PaymentNotFoundException::new);

        if (payment.getStatus() != Payment.PaymentStatus.SUCCESS) {
            throw new PaymentException(
                    "Refund only allowed for successful payments.", "REFUND_NOT_ALLOWED");
        }

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        payment.setRefundAmount(payment.getAmount());
        payment.setRefundedAt(Instant.now());
        Payment saved = paymentRepository.save(payment);

        eventProducer.publishPaymentRefunded(saved);
        log.info("Refund initiated for paymentId={} orderId={}", paymentId, payment.getOrderId());

        return paymentMapper.toResponse(saved);
    }
}