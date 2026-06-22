package com.emras.product.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
/**
 * A product variant = one unique size/color combination.
 * Each variant has its own SKU and is tracked separately in Inventory Service.
 *
 * Example:
 *  Product: "Classic White Shirt"
 *  Variant 1: size=M, color=White → SKU: EMRAS-CWS-M-WHT
 *  Variant 2: size=L, color=White → SKU: EMRAS-CWS-L-WHT
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_variants", schema = "schema_product",
        indexes = @Index(name = "idx_variants_sku", columnList = "sku", unique = true))
public class ProductVariant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @Column(nullable = false, unique = true, length = 100)
    private String sku;
    @Column(length = 20)
    private String size;
    @Column(length = 50)
    private String color;
    /**
     * Price adjustment added to product base price.
     * Example: base price 800 + variantPrice 50 = 850 for this variant.
     * Default 0 = same price as the base product.
     */
    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal additionalPrice = BigDecimal.ZERO;
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}