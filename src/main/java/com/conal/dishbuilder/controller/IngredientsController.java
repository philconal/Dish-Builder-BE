package com.conal.dishbuilder.controller;

import com.conal.dishbuilder.constant.Constants;
import com.conal.dishbuilder.dto.CreateIngredientsRequest;
import com.conal.dishbuilder.dto.UpdateIngredientsRequest;
import com.conal.dishbuilder.dto.request.filter.IngredientsFilterRequest;
import com.conal.dishbuilder.dto.response.BaseResponse;
import com.conal.dishbuilder.dto.response.IngredientsResponse;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.service.IngredientsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = Constants.Endpoint.INGREDIENTS)
@Tag(name = "Ingredients Management", description = "APIs for managing ingredients")
public class IngredientsController {
    private final IngredientsService ingredientsService;

    @PostMapping("/")
    @Operation(summary = "Create a new ingredient", description = "Create a new ingredient for the current tenant")
    public ResponseEntity<BaseResponse<IngredientsResponse>> addIngredients(@Valid @RequestBody CreateIngredientsRequest ingredients) {
        return ResponseEntity.ok(BaseResponse.ok(ingredientsService.addIngredients(ingredients)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ingredient by ID", description = "Retrieve a specific ingredient by its ID")
    public ResponseEntity<BaseResponse<IngredientsResponse>> getIngredientsById(
            @Parameter(description = "Ingredient ID") @PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(ingredientsService.getIngredientsById(id)));
    }

    @GetMapping("/")
    @Operation(summary = "Get ingredients with filtering and pagination", description = "Retrieve ingredients with optional filtering and pagination")
    public ResponseEntity<BaseResponse<PageResponse<IngredientsResponse>>> getIngredients(
            @Parameter(description = "Filter and pagination parameters") IngredientsFilterRequest filterRequest) {
        return ResponseEntity.ok(BaseResponse.ok(ingredientsService.getIngredients(filterRequest)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update ingredient", description = "Update an existing ingredient")
    public ResponseEntity<BaseResponse<IngredientsResponse>> updateIngredients(
            @Parameter(description = "Ingredient ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateIngredientsRequest ingredients) {
        return ResponseEntity.ok(BaseResponse.ok(ingredientsService.updateIngredients(id, ingredients)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete ingredient", description = "Delete an ingredient by its ID")
    public ResponseEntity<BaseResponse<Boolean>> deleteIngredients(
            @Parameter(description = "Ingredient ID") @PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(ingredientsService.deleteIngredients(id)));
    }
}
