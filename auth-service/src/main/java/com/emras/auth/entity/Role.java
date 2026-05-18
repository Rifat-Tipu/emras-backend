package com.emras.auth.entity;
import jakarta.persistence.*;
import lombok.*;
/**
 * Role entity — CUSTOMER, ADMIN, STAFF, VIEWER.
 * Seeded by Flyway migration V2.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles", schema = "schema_auth")
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    private RoleName name;
    public enum RoleName {
        ROLE_CUSTOMER,
        ROLE_ADMIN,
        ROLE_STAFF,
        ROLE_VIEWER
    }
}