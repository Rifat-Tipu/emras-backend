package com.emras.inventory.service.impl;
import com.emras.inventory.constant.ErrorMessages;
import com.emras.inventory.dto.request.CreateInventoryItemRequest;
import com.emras.inventory.dto.request.StockAdjustRequest;
import com.emras.inventory.dto.response.InventoryItemResponse;
import com.emras.inventory.dto.response.StockAvailabilityResponse;
import com.emras.inventory.entity.InventoryAuditLog;
import com.emras.inventory.entity.InventoryItem;
import com.emras.inventory.exception.InsufficientStockException;
import com.emras.inventory.exception.InventoryException;
import com.emras.inventory.exception.InventoryItemNotFoundException;
import com.emras.inventory.kafka.producer.InventoryEventProducer;
import com.emras.inventory.mapper.InventoryMapper;
import com.emras.inventory.repository.InventoryAuditLogRepository;
import com.emras.inventory.repository.InventoryItemRepository;
import com.emras.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryItemRepository     itemRepository;
    private final InventoryAuditLogRepository auditRepository;
    private final InventoryMapper             mapper;
    private final InventoryEventProducer      eventProducer;
    // ── Public ────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public StockAvailabilityResponse checkAvailability(String sku) {
        return mapper.toAvailabilityResponse(
                itemRepository.findBySku(sku)
                        .orElseThrow(() -> new InventoryItemNotFoundException(sku)));
    }
    @Override
    @Transactional(readOnly = true)
    public InventoryItemResponse getStockBySku(String sku) {
        return mapper.toResponse(
                itemRepository.findBySku(sku)
                        .orElseThrow(() -> new InventoryItemNotFoundException(sku)));
    }
    // ── Admin ─────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public InventoryItemResponse createItem(CreateInventoryItemRequest request) {
        if (itemRepository.existsBySku(request.sku())) {
            throw new InventoryException(
                    ErrorMessages.SKU_ALREADY_EXISTS + ": " + request.sku(), "SKU_EXISTS");
        }

        InventoryItem item = InventoryItem.builder()
                .sku(request.sku())
                .productName(request.productName())
                .productVariantId(request.productVariantId())
                .quantity(request.initialQuantity())
                .lowStockThreshold(request.lowStockThreshold())
                .build();

        InventoryItem saved = itemRepository.save(item);

        auditLog(saved.getSku(), null, InventoryAuditLog.MovementType.STOCK_IN,
                request.initialQuantity(), 0, saved.getQuantity(), "Initial stock");

        // Stock alert check — written to outbox (same transaction)
        publishStockAlertsIfNeeded(saved);

        log.info("Inventory created: sku={} qty={}", saved.getSku(), saved.getQuantity());
        return mapper.toResponse(saved);
    }
    @Override
    @Transactional
    public List<InventoryItemResponse> createBulk(List<CreateInventoryItemRequest> requests) {
        return requests.stream().map(this::createItem).toList();
    }
    @Override
    @Transactional
    public InventoryItemResponse addStock(String sku, StockAdjustRequest request) {
        InventoryItem item = itemRepository.findBySku(sku)
                .orElseThrow(() -> new InventoryItemNotFoundException(sku));

        int before = item.getQuantity();
        item.setQuantity(before + request.quantity());
        InventoryItem saved = itemRepository.save(item);

        auditLog(sku, null, InventoryAuditLog.MovementType.STOCK_IN,
                request.quantity(), before, saved.getQuantity(), request.notes());

        publishStockAlertsIfNeeded(saved);

        log.info("Stock added: sku={} +{} total={}", sku, request.quantity(), saved.getQuantity());
        return mapper.toResponse(saved);
    }
    @Override
    @Transactional
    public InventoryItemResponse adjustStock(String sku, StockAdjustRequest request) {
        InventoryItem item = itemRepository.findBySku(sku)
                .orElseThrow(() -> new InventoryItemNotFoundException(sku));

        int before    = item.getQuantity();
        int newQty    = before + request.quantity();

        if (newQty < 0) {
            throw new InventoryException(
                    "Adjustment would result in negative stock.", "NEGATIVE_STOCK");
        }

        item.setQuantity(newQty);
        InventoryItem saved = itemRepository.save(item);

        auditLog(sku, null, InventoryAuditLog.MovementType.STOCK_ADJUST,
                request.quantity(), before, saved.getQuantity(), request.notes());

        return mapper.toResponse(saved);
    }
    // ── Kafka-triggered operations ─────────────────────────────────────────
    @Override
    @Transactional
    public void reserveStock(Long orderId, String sku, int quantity) {
        InventoryItem item = itemRepository.findBySkuForUpdate(sku)
                .orElseThrow(() -> new InventoryItemNotFoundException(sku));

        if (item.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException(sku, quantity, item.getAvailableQuantity());
        }

        int before = item.getReservedQuantity();
        item.setReservedQuantity(before + quantity);
        itemRepository.save(item);

        auditLog(sku, orderId, InventoryAuditLog.MovementType.RESERVED,
                quantity, before, item.getReservedQuantity(), "Order " + orderId);

        // Written to outbox in same transaction — published by scheduler after commit
        publishStockAlertsIfNeeded(item);

        log.info("Reserved: sku={} qty={} orderId={}", sku, quantity, orderId);
    }
    @Override
    @Transactional
    public void releaseReservation(Long orderId, String sku, int quantity) {
        InventoryItem item = itemRepository.findBySku(sku)
                .orElseThrow(() -> new InventoryItemNotFoundException(sku));

        int before = item.getReservedQuantity();
        item.setReservedQuantity(Math.max(0, before - quantity));
        itemRepository.save(item);

        auditLog(sku, orderId, InventoryAuditLog.MovementType.RELEASED,
                quantity, before, item.getReservedQuantity(), "Order cancelled: " + orderId);

        log.info("Released: sku={} qty={} orderId={}", sku, quantity, orderId);
    }
    @Override
    @Transactional
    public void deductStock(Long orderId, String sku, int quantity) {
        InventoryItem item = itemRepository.findBySku(sku)
                .orElseThrow(() -> new InventoryItemNotFoundException(sku));

        int beforeQty      = item.getQuantity();
        int beforeReserved = item.getReservedQuantity();

        item.setQuantity(beforeQty - quantity);
        item.setReservedQuantity(Math.max(0, beforeReserved - quantity));
        itemRepository.save(item);

        auditLog(sku, orderId, InventoryAuditLog.MovementType.DEDUCTED,
                -quantity, beforeQty, item.getQuantity(), "Payment confirmed: " + orderId);

        publishStockAlertsIfNeeded(item);

        log.info("Deducted: sku={} -{} orderId={}", sku, quantity, orderId);
    }
    // ── Private helpers ───────────────────────────────────────────────────
    /**
     * Writes stock alert events to the outbox table — NOT directly to Kafka.
     * Called inside @Transactional — outbox write is part of the same DB commit.
     * OutboxPublisherService scheduler picks these up and sends to Kafka.
     */
    private void publishStockAlertsIfNeeded(InventoryItem item) {
        if (!item.isInStock()) {
            eventProducer.publishOutOfStock(item.getSku(), item.getProductVariantId());
        } else if (item.isLowStock()) {
            eventProducer.publishLowStock(
                    item.getSku(), item.getAvailableQuantity(), item.getLowStockThreshold());
        }
    }
    private void auditLog(String sku, Long orderId, InventoryAuditLog.MovementType type,
                          int change, int before, int after, String notes) {
        auditRepository.save(InventoryAuditLog.builder()
                .sku(sku).orderId(orderId).movementType(type)
                .quantityChange(change).quantityBefore(before)
                .quantityAfter(after).notes(notes).build());
    }
}