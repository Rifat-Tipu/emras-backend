package com.emras.inventory.controller;
import com.emras.inventory.constant.ApiEndpointConstant;
import com.emras.inventory.constant.SuccessMessages;
import com.emras.inventory.dto.response.InventoryItemResponse;
import com.emras.inventory.dto.response.StockAvailabilityResponse;
import com.emras.inventory.model.ApiResponse;
import com.emras.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Public endpoints — no auth required */
@RestController
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Stock availability endpoints")
public class InventoryController {
    private final InventoryService inventoryService;
    @GetMapping(ApiEndpointConstant.STOCK_CHECK)
    @Operation(summary = "Check if a SKU is in stock")
    public ResponseEntity<ApiResponse<StockAvailabilityResponse>> checkAvailability(
            @PathVariable String sku) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.AVAILABILITY_OK,
                inventoryService.checkAvailability(sku),
                HttpStatus.OK));
    }
    @GetMapping(ApiEndpointConstant.STOCK_BY_SKU)
    @Operation(summary = "Get stock details for a SKU")
    public ResponseEntity<ApiResponse<InventoryItemResponse>> getStock(
            @PathVariable String sku) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.STOCK_FETCHED,
                inventoryService.getStockBySku(sku),
                HttpStatus.OK));
    }
}