package com.emras.auth.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
/**
 * Persisted refresh token record.
 * Also whitelisted in Redis for fast validation.
 * DB record provides audit trail; Redis provides speed.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "refresh_tokens",
        schema = "schema_auth",
        indexes = {
                @Index(name = "idx_refresh_token_user", columnList = "user_id"),
                @Index(name = "idx_refresh_token_jti",  columnList = "jti", unique = true)
        }
)
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** JWT ID — unique identifier for this token (used as Redis key) */
    @Column(nullable = false, unique = true, length = 100)
    private String jti;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean revoked = false;

    /** Device/client info for session management */
    @Column(length = 255)
    private String userAgent;

    @Column(length = 50)
    private String ipAddress;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }
}