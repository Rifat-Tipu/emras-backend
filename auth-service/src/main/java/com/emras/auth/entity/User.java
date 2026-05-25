package com.emras.auth.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
/**
 * Core user entity for the Auth Service.
 * Stored in schema_auth.users table.
 *
 * Note: password is nullable to support OAuth2-only accounts.
 * authProvider distinguishes how the account was created.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "users",
        schema = "schema_auth",
        indexes = {
                @Index(name = "idx_users_email",  columnList = "email",  unique = true),
                @Index(name = "idx_users_phone",  columnList = "phone", unique = true)
        }
)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String email;
    /** Null for OAuth2-only accounts */
    @Column(length = 255)
    private String password;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AuthProvider authProvider = AuthProvider.LOCAL;

    /** External ID from Google/Facebook OAuth2 */
    @Column(length = 255)
    private String providerId;

    @Column(nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean accountLocked = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            schema = "schema_auth",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    public enum AuthProvider {
        LOCAL,
        GOOGLE,
        FACEBOOK
    }
}