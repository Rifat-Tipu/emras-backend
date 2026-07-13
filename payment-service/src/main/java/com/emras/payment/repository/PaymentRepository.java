package com.emras.payment.repository;

import com.emras.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderIdOrderByCreatedAtDesc(Long orderId);

    Optional<Payment> findTopByOrderIdAndStatusOrderByCreatedAtDesc(
            Long orderId, Payment.PaymentStatus status);

    boolean existsByOrderIdAndStatus(Long orderId, Payment.PaymentStatus status);
}