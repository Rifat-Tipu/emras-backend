package com.emras.inventory.repository;
import com.emras.inventory.entity.InventoryItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    Optional<InventoryItem> findBySku(String sku);
    boolean existsBySku(String sku);

    /**
     * Optimistic read — used for reservations.
     * The @Version field handles concurrency automatically.
     */
    @Query("SELECT i FROM InventoryItem i WHERE i.sku = :sku")
    Optional<InventoryItem> findBySkuForUpdate(@Param("sku") String sku);

    /** Find all items below their low stock threshold */
    @Query("SELECT i FROM InventoryItem i WHERE (i.quantity - i.reservedQuantity) <= i.lowStockThreshold AND (i.quantity - i.reservedQuantity) > 0")
    List<InventoryItem> findLowStockItems();

    /** Find all items completely out of stock */
    @Query("SELECT i FROM InventoryItem i WHERE (i.quantity - i.reservedQuantity) <= 0")
    List<InventoryItem> findOutOfStockItems();
}