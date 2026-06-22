package com.emras.product.repository;
import com.emras.product.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProductIdAndActiveTrue(Long productId);
    Optional<ProductVariant> findBySkuAndActiveTrue(String sku);
    boolean existsBySku(String sku);
}