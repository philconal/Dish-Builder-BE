package com.conal.dishbuilder.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenant_customization", schema = "dish_builder_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantCustomizationEntity extends Auditable<UUID> implements Serializable {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "primary_color", length = 10, nullable = false)
    private String primaryColor;

    @Column(name = "secondary_color", length = 10, nullable = false)
    private String secondaryColor;

    @Column(name = "third_color", length = 10, nullable = false)
    private String thirdColor;

    @Column(name = "background_image_url", length = 255, nullable = false)
    private String backgroundImageUrl;

    @Column(name = "font_style", length = 255, nullable = false)
    private String fontStyle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private TenantEntity tenant;
}
