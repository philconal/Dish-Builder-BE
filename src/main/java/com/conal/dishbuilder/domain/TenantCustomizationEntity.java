package com.conal.dishbuilder.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class TenantCustomizationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String primaryColor;
    private String  secondaryColor;
    private String thirdColor;
    private String backgroundImageUrl;
    private String fontStyle;
    private String name;
}
