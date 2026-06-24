package com.emras.inventory.repository;
import com.emras.inventory.entity.InventoryAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryAuditLogRepository extends JpaRepository<InventoryAuditLog, Long> {
    Page<InventoryAuditLog> findBySkuOrderByCreatedAtDesc(String sku, Pageable pageable);
    Page<InventoryAuditLog> findByOrderIdOrderByCreatedAtDesc(Long orderId, Pageable pageable);
}