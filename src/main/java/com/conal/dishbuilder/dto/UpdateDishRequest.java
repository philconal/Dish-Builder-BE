package com.conal.dishbuilder.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class UpdateDishRequest {
    @Size(min = 1, max = 100, message = "Name must be in range 1 to 100 character")
    private String name;
    
    @Size(min = 1, max = 255, message = "Description must be in range 1 to 255 character")
    private String description;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Total price must be greater than or equal to 0")
    private BigDecimal totalPrice;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Discount must be greater than or equal to 0")
    private BigDecimal discount;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "VAT must be greater than or equal to 0")
    private BigDecimal vat;
    
    private UUID userId;
    
    private List<UUID> ingredientIds;
}
