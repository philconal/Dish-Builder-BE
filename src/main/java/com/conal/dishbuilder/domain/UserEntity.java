package com.conal.dishbuilder.domain;

import com.conal.dishbuilder.constant.CommonStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "user", schema = "dish_builder_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity extends Auditable<UUID> implements Serializable {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 255, unique = true)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "first_name", length = 255, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 255, nullable = false)
    private String lastName;

    @Column(length = 255, nullable = false)
    private String phone;

    @Column(length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "register_with", columnDefinition = "int default 0")
    private Integer registerWith = 0;

    @Column(name = "status", nullable = false)
    private CommonStatus status = CommonStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false, insertable = false, updatable = false)
    private TenantEntity tenant;
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role", schema = "dish_builder_schema", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles;

}
