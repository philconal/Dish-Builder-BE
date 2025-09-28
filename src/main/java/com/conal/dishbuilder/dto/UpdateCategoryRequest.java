package com.conal.dishbuilder.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCategoryRequest {
    @Size(min = 1, max = 100, message = "Name must be in range 1 to 100 character")
    private String name;
    
    @Size(min = 1, max = 255, message = "Description must be in range 1 to 255 character")
    private String description;
}
