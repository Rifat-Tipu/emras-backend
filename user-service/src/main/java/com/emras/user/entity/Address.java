package com.emras.user.entity;
import jakarta.persistence.*;
import lombok.*;
/**
 * Customer delivery address with Bangladesh-specific fields.
 * A user can have a maximum of 5 addresses.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "addresses", schema = "schema_user",
        indexes = @Index(name = "idx_addresses_user_id", columnList = "user_id"))
public class Address extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private AddressLabel label = AddressLabel.HOME;
    @Column(nullable = false, length = 50)
    private String division;
    @Column(nullable = false, length = 50)
    private String district;
    @Column(nullable = false, length = 100)
    private String thana;
    @Column(length = 255)
    private String area;
    @Column(nullable = false, length = 255)
    private String houseNumber;
    @Column(length = 10)
    private String postalCode;
    /**
     * Changed from primitive boolean to Boolean wrapper.
     * Reason: Lombok generates isIsDefault() for Boolean which MapStruct
     * correctly resolves as property "isDefault".
     * For primitive boolean isDefault, Lombok generates isDefault() which
     * MapStruct reads as property "default" — a reserved keyword that fails.
     */
    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;
    public enum AddressLabel { HOME, OFFICE, OTHER }
}