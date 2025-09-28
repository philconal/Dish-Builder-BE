package com.conal.dishbuilder.validator;

import com.conal.dishbuilder.domain.CategoryEntity;
import com.conal.dishbuilder.dto.CreateCategoryRequest;
import com.conal.dishbuilder.dto.UpdateCategoryRequest;
import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CategoryValidator {
    List<FieldErrorResponse> validateCreateCategory(CreateCategoryRequest request);
    List<FieldErrorResponse> validateUpdateCategory(UpdateCategoryRequest request, CategoryEntity category);
}