package com.conal.dishbuilder.service;

import com.conal.dishbuilder.dto.CreateCategoryRequest;
import com.conal.dishbuilder.dto.UpdateCategoryRequest;
import com.conal.dishbuilder.dto.request.filter.CategoryFilterRequest;
import com.conal.dishbuilder.dto.response.CategoryResponse;
import com.conal.dishbuilder.dto.response.PageResponse;

import java.util.UUID;

public interface CategoryService {
    CategoryResponse addCategory(CreateCategoryRequest category);
    CategoryResponse updateCategory(UUID id, UpdateCategoryRequest category);
    CategoryResponse getCategoryById(UUID id);
    PageResponse<CategoryResponse> getCategories(CategoryFilterRequest filterRequest);
    boolean deleteCategory(UUID id);
}
