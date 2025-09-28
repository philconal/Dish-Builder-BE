package com.conal.dishbuilder.validator;

import com.conal.dishbuilder.context.TenantContextHolder;
import com.conal.dishbuilder.domain.CategoryEntity;
import com.conal.dishbuilder.domain.IngredientsEntity;
import com.conal.dishbuilder.dto.CreateIngredientsRequest;
import com.conal.dishbuilder.dto.UpdateIngredientsRequest;
import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import com.conal.dishbuilder.repository.CategoryRepository;
import com.conal.dishbuilder.repository.IngredientsRepository;
import com.conal.dishbuilder.util.CommonUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.conal.dishbuilder.util.CommonUtils.buildFieldErrorResponse;

@Component
@RequiredArgsConstructor
public class IngredientsValidatorImpl implements IngredientsValidator {
    private final IngredientsRepository ingredientsRepository;
    private final CategoryRepository categoryRepository;
    private final Validator validator;

    @Override
    public List<FieldErrorResponse> validateCreateIngredients(CreateIngredientsRequest request) {
        UUID tenantId = TenantContextHolder.getTenantContext();
        
        Set<ConstraintViolation<Object>> violations = validator.validate(request);
        var fieldErrors = violations.stream()
                .map(CommonUtils::buildFieldErrorResponse).collect(Collectors.toList());

        // Validate name uniqueness within tenant
        if (ingredientsRepository.existsByNameAndTenantId(request.getName().trim(), tenantId)) {
            fieldErrors.add(buildFieldErrorResponse("name", request.getName(), "Name already exists."));
        }

        // Validate category exists and belongs to current tenant
        if (request.getCategoryId() != null && !categoryRepository.existsByIdAndTenantId(request.getCategoryId(), tenantId)) {
            fieldErrors.add(buildFieldErrorResponse("categoryId", request.getCategoryId().toString(), "CategoryId does not exists."));
        }

        return fieldErrors;
    }

    @Override
    public List<FieldErrorResponse> validateUpdateIngredients(UpdateIngredientsRequest request, IngredientsEntity ingredients) {
        UUID tenantId = TenantContextHolder.getTenantContext();
        
        Set<ConstraintViolation<Object>> violations = validator.validate(request);
        var fieldErrors = violations.stream()
                .map(CommonUtils::buildFieldErrorResponse).collect(Collectors.toList());

        // Validate name uniqueness if name is being updated
        if (request.getName() != null && !request.getName().trim().equals(ingredients.getName())) {
            if (ingredientsRepository.existsByNameAndTenantId(request.getName().trim(), tenantId)) {
                fieldErrors.add(buildFieldErrorResponse("name", request.getName(), "Name already exists."));
            }
        }

        // Validate name length if provided
        if (request.getName() != null && (request.getName().trim().length() < 1 || request.getName().trim().length() > 100)) {
            fieldErrors.add(buildFieldErrorResponse("name", request.getName(), "Name must be in range 1 to 100 character"));
        }

        // Validate description length if provided
        if (request.getDescription() != null && (request.getDescription().trim().length() < 1 || request.getDescription().trim().length() > 255)) {
            fieldErrors.add(buildFieldErrorResponse("description", request.getDescription(), "Description must be in range 1 to 255 character"));
        }

        // Validate price if provided
        if (request.getPrice() != null && request.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            fieldErrors.add(buildFieldErrorResponse("price", request.getPrice().toString(), "Price must be greater than 0"));
        }

        // Validate category exists and belongs to current tenant if provided
        if (request.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(request.getCategoryId()).orElse(null);
            if (category == null) {
                fieldErrors.add(buildFieldErrorResponse("categoryId", request.getCategoryId().toString(), "Category not found."));
            } else if (!category.getTenantId().equals(tenantId)) {
                fieldErrors.add(buildFieldErrorResponse("categoryId", request.getCategoryId().toString(), "Category does not belong to current tenant."));
            }
        }

        return fieldErrors;
    }
}
