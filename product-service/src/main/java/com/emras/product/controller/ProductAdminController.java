package com.emras.product.controller;
import com.emras.product.constant.ApiEndpointConstant;
import com.emras.product.constant.ErrorMessages;
import com.emras.product.constant.SuccessMessages;
import com.emras.product.dto.request.CreateProductRequest;
import com.emras.product.dto.request.CreateProductVariantRequest;
import com.emras.product.dto.request.UpdateProductStatusRequest;
import com.emras.product.dto.response.ProductImageResponse;
import com.emras.product.dto.response.ProductResponse;
import com.emras.product.dto.response.ProductVariantResponse;
import com.emras.product.exception.ProductException;
import com.emras.product.model.ApiResponse;
import com.emras.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/** Admin-only product management endpoints. Requires ROLE_ADMIN in X-User-Role header. */
@RestController
@RequiredArgsConstructor
@Tag(name = "Admin — Products", description = "Product management for admins")
public class ProductAdminController {
    private final ProductService productService;
    private void requireAdmin(String userRole) {
        if (userRole == null || !Arrays.asList(userRole.split(",")).contains("ROLE_ADMIN")) {
            throw new ProductException(ErrorMessages.ACCESS_DENIED, "ACCESS_DENIED");
        }
    }
    @PostMapping(ApiEndpointConstant.ADMIN_PRODUCTS)
    @Operation(summary = "Create a new product (Admin only)")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody CreateProductRequest request) {
        requireAdmin(userRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                SuccessMessages.PRODUCT_CREATED,
                productService.createProduct(request),
                HttpStatus.CREATED));
    }
    @PutMapping(ApiEndpointConstant.ADMIN_PRODUCT_BY_ID)
    @Operation(summary = "Update a product (Admin only)")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long id,
            @Valid @RequestBody CreateProductRequest request) {

        requireAdmin(userRole);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.PRODUCT_UPDATED,
                productService.updateProduct(id, request),
                HttpStatus.OK));
    }
    @PatchMapping(ApiEndpointConstant.ADMIN_PRODUCT_STATUS)
    @Operation(summary = "Update product status: DRAFT, ACTIVE, ARCHIVED (Admin only)")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductStatusRequest request) {

        requireAdmin(userRole);
        productService.updateProductStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.STATUS_UPDATED, HttpStatus.OK));
    }
    @DeleteMapping(ApiEndpointConstant.ADMIN_PRODUCT_BY_ID)
    @Operation(summary = "Archive a product (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long id) {

        requireAdmin(userRole);
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.PRODUCT_DELETED, HttpStatus.OK));
    }
    @PostMapping(ApiEndpointConstant.ADMIN_PRODUCT_VARIANTS)
    @Operation(summary = "Add a variant (size/color/SKU) to a product (Admin only)")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> addVariant(
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long productId,
            @Valid @RequestBody CreateProductVariantRequest request) {

        requireAdmin(userRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                SuccessMessages.VARIANT_CREATED,
                productService.addVariant(productId, request),
                HttpStatus.CREATED));
    }
    @DeleteMapping(ApiEndpointConstant.ADMIN_PRODUCT_VARIANTS + "/{variantId}")
    @Operation(summary = "Remove a product variant (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteVariant(
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long productId,
            @PathVariable Long variantId) {

        requireAdmin(userRole);
        productService.deleteVariant(productId, variantId);
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.VARIANT_DELETED, HttpStatus.OK));
    }
}