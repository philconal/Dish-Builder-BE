package com.conal.dishbuilder.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Entity
@Table(name = "dishes", schema = "dish_builder_schema")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class DishEntity extends Auditable<UUID> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String description;

    private UUID tenantId;

    private BigDecimal totalPrice; // price after vat

    private BigDecimal discount;

    private BigDecimal vat;

    /**
     * Each dish belongs to one user (customer).
     * The foreign key is "user_id" in the dishes table.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_dish_user")
    )
    private UserEntity user;

    /**
     * Many dishes can have many ingredients.
     * We use a join table "dish_ingredients" to store the relationship.
     */
    @ManyToMany
    @JoinTable(
            name = "dish_ingredients",
            schema = "dish_builder_schema",
            joinColumns = @JoinColumn(name = "dish_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<IngredientsEntity> ingredients = new ArrayList<>();
}
