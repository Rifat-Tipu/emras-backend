package com.emras.inventory.repository;
import com.emras.inventory.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {
    boolean existsByEventIdAndConsumerGroup(String eventId, String consumerGroup);
}