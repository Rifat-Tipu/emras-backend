package com.emras.product.mapper;
import com.emras.product.dto.response.ProductImageResponse;
import com.emras.product.dto.response.ProductResponse;
import com.emras.product.dto.response.ProductSummaryResponse;
import com.emras.product.dto.response.ProductVariantResponse;
import com.emras.product.entity.Product;
import com.emras.product.entity.ProductImage;
import com.emras.product.entity.ProductVariant;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {CategoryMapper.class})
public interface ProductMapper {

    @Mapping(target = "category",  source = "category")
    @Mapping(target = "variants",  source = "variants")
    @Mapping(target = "images",    source = "images")
    ProductResponse toResponse(Product product);

    @Mapping(target = "primaryImageUrl",  expression = "java(getPrimaryImageUrl(product))")
    @Mapping(target = "categoryNameEn",   source = "category.nameEn")
    ProductSummaryResponse toSummaryResponse(Product product);

    List<ProductSummaryResponse> toSummaryResponseList(List<Product> products);

    ProductVariantResponse toVariantResponse(ProductVariant variant);

    ProductImageResponse toImageResponse(ProductImage image);

    default String getPrimaryImageUrl(Product product) {
        // Extra defensive check: return null immediately if product or its image list is null
        if (product == null || product.getImages() == null) {
            return null;
        }
        return product.getImages().stream()
                // 1. Safely checks for 'true' without throwing NullPointerException if isPrimary is null
                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                .findFirst()
                // 2. Extracts the URL if a primary image was found
                .map(ProductImage::getUrl)
                // 3. Lazy fallback: This lambda expression runs ONLY if no primary image exists
                .orElseGet(() -> product.getImages().isEmpty() ? null
                        : product.getImages().get(0).getUrl());
    }
}