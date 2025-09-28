package com.conal.dishbuilder.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ingredients", schema = "dish_builder_schema")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class IngredientsEntity extends Auditable<UUID> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String description;

    private UUID tenantId;

    private BigDecimal price;

    /**
     * Many ingredients belong to one category.
     * Using LAZY fetch to avoid unnecessary joins.
     * The foreign key column is "category_id".
     */
    @ManyToOne(optional = false)
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ingredient_category")
    )
    private CategoryEntity category;
}

