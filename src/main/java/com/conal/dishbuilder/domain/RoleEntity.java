package com.conal.dishbuilder.domain;

import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "role", schema = "dish_builder_schema")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class RoleEntity extends Auditable<UUID> implements Serializable {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 255, nullable = false)
    private String description;

    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users;
}
