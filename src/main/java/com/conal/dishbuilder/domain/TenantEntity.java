package com.conal.dishbuilder.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tenant", schema = "dish_builder_schema")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TenantEntity extends Auditable<UUID> implements Serializable {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 15, nullable = false)
    private String phone;

    @Column(length = 150, nullable = false)
    private String email;

    @Column(length = 255)
    private String address;

    @Column(name = "url_slug", length = 255, nullable = false)
    private String urlSlug;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "sub_domain", length = 255)
    private String subDomain;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private Set<TenantCustomizationEntity> customizations;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private Set<UserEntity> users;

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL)
    private Set<RoleEntity> roles;
}
