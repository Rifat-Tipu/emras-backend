package com.emras.user.entity;
import jakarta.persistence.*;
import lombok.*;
/**
 * Wishlist item — links a user to a product ID.
 * Product details are fetched from Product Service when needed.
 * We only store the productId here (no cross-service join).
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wishlist_items", schema = "schema_user",
        indexes = @Index(name = "idx_wishlist_user_id", columnList = "user_id"),
        uniqueConstraints = @UniqueConstraint(
                name = "uk_wishlist_user_product",
                columnNames = {"user_id", "product_id"}))
public class WishlistItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "product_id", nullable = false)
    private Long productId;
}