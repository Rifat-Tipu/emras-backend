package com.emras.product.controller;
import com.emras.product.constant.ApiEndpointConstant;
import com.emras.product.constant.SuccessMessages;
import com.emras.product.dto.request.CreateCategoryRequest;
import com.emras.product.dto.response.CategoryResponse;
import com.emras.product.model.ApiResponse;
import com.emras.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Product category endpoints")
public class CategoryController {
    private final CategoryService categoryService;
    @GetMapping(ApiEndpointConstant.CATEGORIES)
    @Operation(summary = "Get all categories (public)")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.CATEGORIES_FETCHED,
                categoryService.getAllCategories(),
                HttpStatus.OK));
    }
    @GetMapping(ApiEndpointConstant.CATEGORY_BY_ID)
    @Operation(summary = "Get category by ID (public)")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.CATEGORIES_FETCHED,
                categoryService.getCategoryById(id),
                HttpStatus.OK));
    }
    @PostMapping(ApiEndpointConstant.ADMIN_CATEGORIES)
    @Operation(summary = "Create category (Admin only)")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @RequestHeader("X-User-Role") String userRole,
            @Valid @RequestBody CreateCategoryRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                SuccessMessages.CATEGORY_CREATED,
                categoryService.createCategory(request),
                HttpStatus.CREATED));
    }
    @PutMapping(ApiEndpointConstant.ADMIN_CATEGORY_BY_ID)
    @Operation(summary = "Update category (Admin only)")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @RequestHeader("X-User-Role") String userRole,
            @PathVariable Long id,
            @Valid @RequestBody CreateCategoryRequest request) {

        return ResponseEntity.ok(ApiResponse.success(
                SuccessMessages.CATEGORY_UPDATED,
                categoryService.updateCategory(id, request),
                HttpStatus.OK));
    }
}