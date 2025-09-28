package com.conal.dishbuilder.controller;

import com.conal.dishbuilder.constant.Constants;
import com.conal.dishbuilder.dto.CreateCategoryRequest;
import com.conal.dishbuilder.dto.UpdateCategoryRequest;
import com.conal.dishbuilder.dto.request.filter.CategoryFilterRequest;
import com.conal.dishbuilder.dto.response.BaseResponse;
import com.conal.dishbuilder.dto.response.CategoryResponse;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.service.CategoryService;
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
@RequestMapping(value = Constants.Endpoint.CATEGORY)
@Tag(name = "Category Management", description = "APIs for managing categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/")
    @Operation(summary = "Create a new category", description = "Create a new category for the current tenant")
    public ResponseEntity<BaseResponse<CategoryResponse>> addCategory(@Valid @RequestBody CreateCategoryRequest category) {
        return ResponseEntity.ok(BaseResponse.ok(categoryService.addCategory(category)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieve a specific category by its ID")
    public ResponseEntity<BaseResponse<CategoryResponse>> getCategoryById(
            @Parameter(description = "Category ID") @PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(categoryService.getCategoryById(id)));
    }

    @GetMapping("/")
    @Operation(summary = "Get categories with filtering and pagination", description = "Retrieve categories with optional filtering and pagination")
    public ResponseEntity<BaseResponse<PageResponse<CategoryResponse>>> getCategories(
            @Parameter(description = "Filter and pagination parameters") CategoryFilterRequest filterRequest) {
        return ResponseEntity.ok(BaseResponse.ok(categoryService.getCategories(filterRequest)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Update an existing category")
    public ResponseEntity<BaseResponse<CategoryResponse>> updateCategory(
            @Parameter(description = "Category ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest category) {
        return ResponseEntity.ok(BaseResponse.ok(categoryService.updateCategory(id, category)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Delete a category by its ID")
    public ResponseEntity<BaseResponse<Boolean>> deleteCategory(
            @Parameter(description = "Category ID") @PathVariable UUID id) {
        return ResponseEntity.ok(BaseResponse.ok(categoryService.deleteCategory(id)));
    }
}
