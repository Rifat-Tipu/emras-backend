package com.emras.product.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_images", schema = "schema_product",
        indexes = @Index(name = "idx_images_product", columnList = "product_id"))
public class ProductImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @Column(nullable = false, length = 500)
    private String url;
    @Column(length = 200)
    private String altText;
    @Column(nullable = false)
    @Builder.Default
    private Boolean isPrimary = false;
    @Column(nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;
}