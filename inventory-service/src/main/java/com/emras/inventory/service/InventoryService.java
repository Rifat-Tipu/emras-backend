package com.emras.inventory.service;
import com.emras.inventory.dto.request.CreateInventoryItemRequest;
import com.emras.inventory.dto.request.StockAdjustRequest;
import com.emras.inventory.dto.response.InventoryItemResponse;
import com.emras.inventory.dto.response.StockAvailabilityResponse;
import java.util.List;
public interface InventoryService {
    // Public
    StockAvailabilityResponse checkAvailability(String sku);
    InventoryItemResponse getStockBySku(String sku);
    // Admin
    InventoryItemResponse createItem(CreateInventoryItemRequest request);
    List<InventoryItemResponse> createBulk(List<CreateInventoryItemRequest> requests);
    InventoryItemResponse addStock(String sku, StockAdjustRequest request);
    InventoryItemResponse adjustStock(String sku, StockAdjustRequest request);
    // Called by Kafka consumers
    void reserveStock(Long orderId, String sku, int quantity);
    void releaseReservation(Long orderId, String sku, int quantity);
    void deductStock(Long orderId, String sku, int quantity);
}