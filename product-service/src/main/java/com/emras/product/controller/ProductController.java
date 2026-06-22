package com.emras.product.controller;
import com.emras.product.constant.ApiEndpointConstant;
import com.emras.product.constant.SuccessMessages;
import com.emras.product.dto.response.PagedResponse;
import com.emras.product.dto.response.ProductResponse;
import com.emras.product.dto.response.ProductSummaryResponse;
import com.emras.product.model.ApiResponse;
import com.emras.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
/** Public product endpoints — no authentication required */
@RestController
@RequestMapping(ApiEndpointConstant.PRODUCTS)
@RequiredArgsConstructor
@Tag(name = "Products", description = "Public product browsing endpoints")
public class ProductController {
    private final ProductService productService;
    @GetMapping
    @Operation(summary = "Get products with optional filters and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<ProductSummaryResponse>>> getProducts(
            @RequestParam(required = false) Long     categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean  featured,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.PRODUCTS_FETCHED,
                productService.getProducts(categoryId, minPrice, maxPrice, featured,
                        PageRequest.of(page, size, sort)),
                HttpStatus.OK));
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get product detail by ID")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.PRODUCT_FETCHED,
                productService.getProductById(id),
                HttpStatus.OK));
    }
    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get product detail by slug")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySlug(
            @PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.PRODUCT_FETCHED,
                productService.getProductBySlug(slug),
                HttpStatus.OK));
    }
}