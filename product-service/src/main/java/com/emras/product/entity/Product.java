package com.emras.product.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products", schema = "schema_product",
        indexes = {
                @Index(name = "idx_products_slug",     columnList = "slug",      unique = true),
                @Index(name = "idx_products_category", columnList = "category_id"),
                @Index(name = "idx_products_status",   columnList = "status"),
                @Index(name = "idx_products_featured", columnList = "featured")
        })
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 200)
    private String nameEn;
    @Column(nullable = false, length = 200)
    private String nameBn;
    @Column(columnDefinition = "TEXT")
    private String descriptionEn;
    @Column(columnDefinition = "TEXT")
    private String descriptionBn;
    /** Unique URL-friendly identifier, e.g. "classic-white-shirt" */
    @Column(nullable = false, unique = true, length = 250)
    private String slug;
    /** Base price in BDT (Bangladeshi Taka) */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    /** Discounted price — null means no active discount */
    @Column(precision = 10, scale = 2)
    private BigDecimal discountPrice;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private ProductStatus status = ProductStatus.DRAFT;
    @Column(nullable = false)
    @Builder.Default
    private Boolean featured = false;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>();
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();
    public enum ProductStatus { DRAFT, ACTIVE, ARCHIVED }
}