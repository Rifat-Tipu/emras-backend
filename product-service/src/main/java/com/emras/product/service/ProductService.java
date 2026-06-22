package com.emras.product.service;
import com.emras.product.dto.request.CreateProductRequest;
import com.emras.product.dto.request.CreateProductVariantRequest;
import com.emras.product.dto.request.UpdateProductStatusRequest;
import com.emras.product.dto.response.*;
import com.emras.product.entity.Product;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
public interface ProductService {
    // Public
    PagedResponse<ProductSummaryResponse> getProducts(Long categoryId, BigDecimal minPrice,
                                                      BigDecimal maxPrice, Boolean featured,
                                                      Pageable pageable);
    ProductResponse getProductById(Long id);
    ProductResponse getProductBySlug(String slug);
    // Admin
    ProductResponse createProduct(CreateProductRequest request);
    ProductResponse updateProduct(Long id, CreateProductRequest request);
    void updateProductStatus(Long id, UpdateProductStatusRequest request);
    void deleteProduct(Long id);
    // Variants
    ProductVariantResponse addVariant(Long productId, CreateProductVariantRequest request);
    void deleteVariant(Long productId, Long variantId);
    // Images
    ProductImageResponse addImage(Long productId, String url, String altText, Boolean isPrimary);
    void deleteImage(Long productId, Long imageId);
}