package com.emras.inventory.repository;
import com.emras.inventory.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    /**
     * Fetch events waiting to be published.
     * Max retries = 5 to avoid infinite loops on bad events.
     * Oldest first to maintain event ordering.
     */
    @Query("""
            SELECT o FROM OutboxEvent o
            WHERE o.status = 'PENDING'
            AND o.retryCount < 5
            ORDER BY o.createdAt ASC
            LIMIT 50
            """)
    List<OutboxEvent> findPendingEvents();
    boolean existsByEventId(String eventId);
}