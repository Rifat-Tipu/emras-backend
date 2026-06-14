package com.emras.user.entity;
import jakarta.persistence.*;
import lombok.*;
/**
 * Customer profile entity.
 *
 * userId is the same ID as in Auth Service's users table.
 * We do NOT do a cross-schema join — userId is just stored as a Long.
 * The link is maintained through Kafka events.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profiles", schema = "schema_user",
        indexes = @Index(name = "idx_user_profiles_user_id", columnList = "user_id", unique = true))
public class UserProfile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** References Auth Service user ID — no JPA join (different schema) */
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    @Column(length = 100)
    private String firstName;
    @Column(length = 100)
    private String lastName;
    @Column(length = 20)
    private String phone;
    @Column(length = 255)
    private String avatarUrl;
    /** EN = English, BN = Bangla */
    @Enumerated(EnumType.STRING)
    @Column(length = 5)
    @Builder.Default
    private Language preferredLanguage = Language.EN;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;
    public enum Language { EN, BN }
    public enum Gender   { MALE, FEMALE, OTHER }
}