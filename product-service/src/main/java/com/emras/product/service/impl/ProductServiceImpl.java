package com.emras.product.service.impl;
import com.emras.product.constant.ErrorMessages;
import com.emras.product.dto.request.CreateProductRequest;
import com.emras.product.dto.request.CreateProductVariantRequest;
import com.emras.product.dto.request.UpdateProductStatusRequest;
import com.emras.product.dto.response.*;
import com.emras.product.entity.*;
import com.emras.product.exception.CategoryNotFoundException;
import com.emras.product.exception.ProductException;
import com.emras.product.exception.ProductNotFoundException;
import com.emras.product.kafka.producer.ProductEventProducer;
import com.emras.product.mapper.ProductMapper;
import com.emras.product.repository.*;
import com.emras.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository        productRepository;
    private final CategoryRepository       categoryRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductImageRepository   imageRepository;
    private final ProductMapper            productMapper;
    private final ProductEventProducer     eventProducer;
    // ── Public endpoints ──────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductSummaryResponse> getProducts(Long categoryId,
                                                             BigDecimal minPrice,
                                                             BigDecimal maxPrice,
                                                             Boolean featured,
                                                             Pageable pageable) {
        Page<Product> page = productRepository.findWithFilters(
                categoryId, minPrice, maxPrice, featured, pageable);

        return new PagedResponse<>(
                productMapper.toSummaryResponseList(page.getContent()),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ProductNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));
        return productMapper.toResponse(product);
    }
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'slug:' + #slug")
    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository
                .findBySlugAndStatus(slug, Product.ProductStatus.ACTIVE)
                .orElseThrow(() -> new ProductNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));
        return productMapper.toResponse(product);
    }
    // ── Admin endpoints ───────────────────────────────────────────────────
    @Override
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.existsBySlug(request.slug())) {
            throw new ProductException(ErrorMessages.SLUG_ALREADY_EXISTS, "SLUG_EXISTS");
        }
        Product product = Product.builder()
                .nameEn(request.nameEn())
                .nameBn(request.nameBn())
                .descriptionEn(request.descriptionEn())
                .descriptionBn(request.descriptionBn())
                .slug(request.slug())
                .price(request.price())
                .discountPrice(request.discountPrice())
                .status(Product.ProductStatus.DRAFT)
                .featured(request.featured() != null ? request.featured() : false)
                .build();

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));
            product.setCategory(category);
        }
        Product saved = productRepository.save(product);
        log.info("Product created: id={} slug={}", saved.getId(), saved.getSlug());

        eventProducer.publishProductUpdated(saved.getId(), saved.getNameEn(),
                saved.getSlug(), saved.getStatus().name());

        return productMapper.toResponse(saved);
    }
    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public ProductResponse updateProduct(Long id, CreateProductRequest request) {
        Product product = productRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ProductNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));

        product.setNameEn(request.nameEn());
        product.setNameBn(request.nameBn());
        product.setDescriptionEn(request.descriptionEn());
        product.setDescriptionBn(request.descriptionBn());
        product.setPrice(request.price());
        product.setDiscountPrice(request.discountPrice());

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));
            product.setCategory(category);
        }
        Product saved = productRepository.save(product);
        eventProducer.publishProductUpdated(saved.getId(), saved.getNameEn(),
                saved.getSlug(), saved.getStatus().name());

        return productMapper.toResponse(saved);
    }
    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public void updateProductStatus(Long id, UpdateProductStatusRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));
        product.setStatus(request.status());
        productRepository.save(product);
        log.info("Product {} status changed to {}", id, request.status());
    }
    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));
        product.setStatus(Product.ProductStatus.ARCHIVED);
        productRepository.save(product);
        log.info("Product {} archived", id);
    }
    // ── Variants ──────────────────────────────────────────────────────────
    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public ProductVariantResponse addVariant(Long productId,
                                             CreateProductVariantRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));

        if (variantRepository.existsBySku(request.sku())) {
            throw new ProductException("SKU already exists: " + request.sku(), "SKU_EXISTS");
        }

        ProductVariant variant = ProductVariant.builder()
                .product(product)
                .sku(request.sku())
                .size(request.size())
                .color(request.color())
                .additionalPrice(request.additionalPrice() != null
                        ? request.additionalPrice() : BigDecimal.ZERO)
                .active(true)
                .build();

        return productMapper.toVariantResponse(variantRepository.save(variant));
    }
    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public void deleteVariant(Long productId, Long variantId) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new ProductNotFoundException(ErrorMessages.VARIANT_NOT_FOUND));
        variant.setActive(false);
        variantRepository.save(variant);
    }
    // ── Images ────────────────────────────────────────────────────────────
    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public ProductImageResponse addImage(Long productId, String url,
                                         String altText, Boolean isPrimary) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND));

        if (Boolean.TRUE.equals(isPrimary)) {
            imageRepository.clearPrimaryForProduct(productId);
        }

        ProductImage image = ProductImage.builder()
                .product(product)
                .url(url)
                .altText(altText)
                .isPrimary(isPrimary != null ? isPrimary : false)
                .displayOrder(product.getImages().size())
                .build();

        return productMapper.toImageResponse(imageRepository.save(image));
    }
    @Override
    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public void deleteImage(Long productId, Long imageId) {
        ProductImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ProductNotFoundException("Image not found."));
        imageRepository.delete(image);
    }
}