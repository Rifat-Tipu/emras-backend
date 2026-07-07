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

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
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
        if (product == null || product.getImages() == null || product.getImages().isEmpty()) {
            return null;
        }
        return product.getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                .findFirst()
                .map(ProductImage::getUrl)
                .orElseGet(() -> product.getImages().stream()
                        .findFirst()
                        .map(ProductImage::getUrl)
                        .orElse(null));
    }
}