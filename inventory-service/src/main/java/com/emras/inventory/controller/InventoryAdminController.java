package com.emras.inventory.controller;
import com.emras.inventory.constant.ApiEndpointConstant;
import com.emras.inventory.constant.ErrorMessages;
import com.emras.inventory.constant.SuccessMessages;
import com.emras.inventory.dto.request.CreateInventoryItemRequest;
import com.emras.inventory.dto.request.StockAdjustRequest;
import com.emras.inventory.dto.response.InventoryItemResponse;
import com.emras.inventory.exception.InventoryException;
import com.emras.inventory.model.ApiResponse;
import com.emras.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;
/** Admin-only inventory management endpoints */
@RestController
@RequiredArgsConstructor
@Tag(name = "Admin — Inventory", description = "Stock management for admins")
public class InventoryAdminController {

    private final InventoryService inventoryService;

    private void requireAdmin(String userRole) {
        if (userRole == null || !Arrays.asList(userRole.split(",")).contains("ROLE_ADMIN")) {
            throw new InventoryException(ErrorMessages.ACCESS_DENIED, "ACCESS_DENIED");
        }
    }
    @PostMapping(ApiEndpointConstant.ADMIN_INVENTORY)
    @Operation(summary = "Create a new inventory item for a SKU (Admin only)")
    public ResponseEntity<ApiResponse<InventoryItemResponse>> createItem(
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody CreateInventoryItemRequest request) {

        requireAdmin(userRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                SuccessMessages.ITEM_CREATED,
                inventoryService.createItem(request),
                HttpStatus.CREATED));
    }
    @PostMapping(ApiEndpointConstant.ADMIN_BULK_CREATE)
    @Operation(summary = "Create multiple inventory items in one request (Admin only)")
    public ResponseEntity<ApiResponse<List<InventoryItemResponse>>> createBulk(
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody List<CreateInventoryItemRequest> requests) {

        requireAdmin(userRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                SuccessMessages.BULK_CREATED,
                inventoryService.createBulk(requests),
                HttpStatus.CREATED));
    }
    @PostMapping(ApiEndpointConstant.ADMIN_STOCK_IN)
    @Operation(summary = "Add stock to an existing SKU (Admin only)")
    public ResponseEntity<ApiResponse<InventoryItemResponse>> addStock(
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable String sku,
            @Valid @RequestBody StockAdjustRequest request) {

        requireAdmin(userRole);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.STOCK_IN,
                inventoryService.addStock(sku, request),
                HttpStatus.OK));
    }
    @PatchMapping(ApiEndpointConstant.ADMIN_STOCK_ADJUST)
    @Operation(summary = "Manually adjust stock (positive or negative) (Admin only)")
    public ResponseEntity<ApiResponse<InventoryItemResponse>> adjustStock(
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable String sku,
            @Valid @RequestBody StockAdjustRequest request) {

        requireAdmin(userRole);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.STOCK_ADJUSTED,
                inventoryService.adjustStock(sku, request),
                HttpStatus.OK));
    }
}