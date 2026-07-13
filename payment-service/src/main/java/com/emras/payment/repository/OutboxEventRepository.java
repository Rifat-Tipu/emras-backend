package com.emras.payment.repository;

import com.emras.payment.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    @Query("""
            SELECT o FROM OutboxEvent o
            WHERE o.status = 'PENDING'
            AND o.retryCount < 5
            ORDER BY o.createdAt ASC
            LIMIT 50
            """)
    List<OutboxEvent> findPendingEvents();
}