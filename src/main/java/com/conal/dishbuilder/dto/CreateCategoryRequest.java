package com.conal.dishbuilder.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCategoryRequest {
    @NotNull
    @Size(min = 1, max = 100, message = "Name must be in range 1 to 100 character")
    private String name;
    @NotNull
    @Size(min = 1, max = 255, message = "Name must be in range 1 to 100 character")
    private String description;
}
