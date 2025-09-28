package com.conal.dishbuilder.controller;

import com.conal.dishbuilder.constant.Constants;
import com.conal.dishbuilder.dto.CreateDishRequest;
import com.conal.dishbuilder.dto.UpdateDishRequest;
import com.conal.dishbuilder.dto.request.filter.DishFilterRequest;
import com.conal.dishbuilder.dto.response.BaseResponse;
import com.conal.dishbuilder.dto.response.DishResponse;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.service.DishService;
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
@RequestMapping(value = Constants.Endpoint.DISH)
@Tag(name = "Dish Management", description = "APIs for managing dishes with ingredients")
public class DishController {
    private final DishService dishService;

    @PostMapping("/")
    @Operation(summary = "Create a new dish", description = "Create a new dish with ingredients for the current tenant")
    public ResponseEntity<BaseResponse<DishResponse>> addDish(@Valid @RequestBody CreateDishRequest dish) {
        return ResponseEntity.ok(BaseResponse.ok(dishService.addDish(dish)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get dish by ID", description = "Retrieve a specific dish by its ID")
    public ResponseEntity<BaseResponse<DishResponse>> getDishById(
            @Parameter(description = "Dish ID") @PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(dishService.getDishById(id)));
    }

    @GetMapping("/")
    @Operation(summary = "Get dishes with filtering and pagination", description = "Retrieve dishes with optional filtering and pagination")
    public ResponseEntity<BaseResponse<PageResponse<DishResponse>>> getDishes(
            @Parameter(description = "Filter and pagination parameters") DishFilterRequest filterRequest) {
        return ResponseEntity.ok(BaseResponse.ok(dishService.getDishes(filterRequest)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update dish", description = "Update an existing dish")
    public ResponseEntity<BaseResponse<DishResponse>> updateDish(
            @Parameter(description = "Dish ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateDishRequest dish) {
        return ResponseEntity.ok(BaseResponse.ok(dishService.updateDish(id, dish)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete dish", description = "Delete a dish by its ID")
    public ResponseEntity<BaseResponse<Boolean>> deleteDish(
            @Parameter(description = "Dish ID") @PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(dishService.deleteDish(id)));
    }
}
