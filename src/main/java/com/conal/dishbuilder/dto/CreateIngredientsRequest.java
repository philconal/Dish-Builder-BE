package com.conal.dishbuilder.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateIngredientsRequest {
    @NotNull
    @Size(min = 1, max = 100, message = "Name must be in range 1 to 100 character")
    private String name;
    
    @NotNull
    @Size(min = 1, max = 255, message = "Description must be in range 1 to 255 character")
    private String description;
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Category ID is required")
    private UUID categoryId;
}
