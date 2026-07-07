package com.emras.product.repository;
import com.emras.product.entity.Product;
import com.emras.product.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySlugAndStatus(String slug, ProductStatus status);

    boolean existsBySlug(String slug);

    @Query("""
            SELECT p FROM Product p
            LEFT JOIN FETCH p.category
            WHERE p.status = 'ACTIVE'
            AND (:categoryId IS NULL OR p.category.id = :categoryId)
            AND (:minPrice IS NULL OR p.price >= :minPrice)
            AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            AND (:featured IS NULL OR p.featured = :featured)
            """)
    Page<Product> findWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("featured") Boolean featured,
            Pageable pageable
    );

    @Query("""

            SELECT p FROM Product p
        LEFT JOIN FETCH p.variants
        WHERE p.id = :id
        """)
    Optional<Product> findByIdWithVariants(@Param("id") Long id);

    @Query("""
        SELECT p FROM Product p
        LEFT JOIN FETCH p.images
        WHERE p.id = :id
        """)
    Optional<Product> findByIdWithImages(@Param("id") Long id);
    }
