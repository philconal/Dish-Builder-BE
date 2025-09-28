package com.conal.dishbuilder.validator;

import com.conal.dishbuilder.context.TenantContextHolder;
import com.conal.dishbuilder.domain.CategoryEntity;
import com.conal.dishbuilder.dto.CreateCategoryRequest;
import com.conal.dishbuilder.dto.UpdateCategoryRequest;
import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import com.conal.dishbuilder.repository.CategoryRepository;
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
public class CategoryValidatorImpl implements CategoryValidator {
    private final CategoryRepository categoryRepository;
    private final Validator validator;

    @Override
    public List<FieldErrorResponse> validateCreateCategory(CreateCategoryRequest request) {
        UUID tenantId = TenantContextHolder.getTenantContext();
        
        Set<ConstraintViolation<Object>> violations = validator.validate(request);
        var fieldErrors = violations.stream()
                .map(CommonUtils::buildFieldErrorResponse).collect(Collectors.toList());

        if (categoryRepository.existsByNameAndTenantId(request.getName().trim(), tenantId)) {
            fieldErrors.add(buildFieldErrorResponse("name", request.getName(), "Name already exists."));
        }
        return fieldErrors;
    }

    @Override
    public List<FieldErrorResponse> validateUpdateCategory(UpdateCategoryRequest request, CategoryEntity category) {
        UUID tenantId = TenantContextHolder.getTenantContext();
        
        Set<ConstraintViolation<Object>> violations = validator.validate(request);
        var fieldErrors = violations.stream()
                .map(CommonUtils::buildFieldErrorResponse).collect(Collectors.toList());

        // Validate name uniqueness if name is being updated
        if (request.getName() != null && !request.getName().trim().equals(category.getName())) {
            if (categoryRepository.existsByNameAndTenantId(request.getName().trim(), tenantId)) {
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

        return fieldErrors;
    }
}
