package com.conal.dishbuilder.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "role", schema = "dish_builder_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity extends Auditable<UUID> implements Serializable {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 255, nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private TenantEntity tenant;

    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users;
}
