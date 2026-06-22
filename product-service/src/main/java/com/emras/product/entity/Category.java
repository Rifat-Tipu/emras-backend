package com.emras.product.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Product category — supports hierarchical structure.
 * Example: Men (parent) → Shirts (child) → Formal Shirts (grandchild)
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categories", schema = "schema_product")
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String nameEn;
    @Column(nullable = false, length = 100)
    private String nameBn;
    @Column(unique = true, nullable = false, length = 150)
    private String slug;
    /** Null means this is a top-level category */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Category> children = new ArrayList<>();
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
    @Column(length = 255)
    private String imageUrl;
}