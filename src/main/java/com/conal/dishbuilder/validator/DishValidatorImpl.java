package com.conal.dishbuilder.validator;

import com.conal.dishbuilder.context.TenantContextHolder;
import com.conal.dishbuilder.domain.DishEntity;
import com.conal.dishbuilder.domain.IngredientsEntity;
import com.conal.dishbuilder.domain.UserEntity;
import com.conal.dishbuilder.dto.CreateDishRequest;
import com.conal.dishbuilder.dto.UpdateDishRequest;
import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import com.conal.dishbuilder.repository.DishRepository;
import com.conal.dishbuilder.repository.IngredientsRepository;
import com.conal.dishbuilder.repository.UserRepository;
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
public class DishValidatorImpl implements DishValidator {
    private final DishRepository dishRepository;
    private final UserRepository userRepository;
    private final IngredientsRepository ingredientsRepository;
    private final Validator validator;

    @Override
    public List<FieldErrorResponse> validateCreateDish(CreateDishRequest request) {
        UUID tenantId = TenantContextHolder.getTenantContext();
        
        Set<ConstraintViolation<Object>> violations = validator.validate(request);
        var fieldErrors = violations.stream()
                .map(CommonUtils::buildFieldErrorResponse).collect(Collectors.toList());

        // Validate name uniqueness within tenant
        if (dishRepository.existsByNameAndTenantId(request.getName().trim(), tenantId)) {
            fieldErrors.add(buildFieldErrorResponse("name", request.getName(), "Name already exists."));
        }

        // Validate user exists and belongs to current tenant
        if (request.getUserId() != null) {
            UserEntity user = userRepository.findById(request.getUserId()).orElse(null);
            if (user == null) {
                fieldErrors.add(buildFieldErrorResponse("userId", request.getUserId().toString(), "User not found."));
            } else if (!user.getTenantId().equals(tenantId)) {
                fieldErrors.add(buildFieldErrorResponse("userId", request.getUserId().toString(), "User does not belong to current tenant."));
            }
        }

        // Validate ingredients exist and belong to current tenant
        if (request.getIngredientIds() != null && !request.getIngredientIds().isEmpty()) {
            for (UUID ingredientId : request.getIngredientIds()) {
                IngredientsEntity ingredient = ingredientsRepository.findById(ingredientId).orElse(null);
                if (ingredient == null) {
                    fieldErrors.add(buildFieldErrorResponse("ingredientIds", ingredientId.toString(), "Ingredient not found."));
                } else if (!ingredient.getTenantId().equals(tenantId)) {
                    fieldErrors.add(buildFieldErrorResponse("ingredientIds", ingredientId.toString(), "Ingredient does not belong to current tenant."));
                }
            }
        }

        return fieldErrors;
    }

    @Override
    public List<FieldErrorResponse> validateUpdateDish(UpdateDishRequest request, DishEntity dish) {
        UUID tenantId = TenantContextHolder.getTenantContext();
        
        Set<ConstraintViolation<Object>> violations = validator.validate(request);
        var fieldErrors = violations.stream()
                .map(CommonUtils::buildFieldErrorResponse).collect(Collectors.toList());

        // Validate name uniqueness if name is being updated
        if (request.getName() != null && !request.getName().trim().equals(dish.getName())) {
            if (dishRepository.existsByNameAndTenantId(request.getName().trim(), tenantId)) {
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

        // Validate total price if provided
        if (request.getTotalPrice() != null && request.getTotalPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            fieldErrors.add(buildFieldErrorResponse("totalPrice", request.getTotalPrice().toString(), "Total price must be greater than or equal to 0"));
        }

        // Validate discount if provided
        if (request.getDiscount() != null && request.getDiscount().compareTo(java.math.BigDecimal.ZERO) < 0) {
            fieldErrors.add(buildFieldErrorResponse("discount", request.getDiscount().toString(), "Discount must be greater than or equal to 0"));
        }

        // Validate VAT if provided
        if (request.getVat() != null && request.getVat().compareTo(java.math.BigDecimal.ZERO) < 0) {
            fieldErrors.add(buildFieldErrorResponse("vat", request.getVat().toString(), "VAT must be greater than or equal to 0"));
        }

        // Validate user exists and belongs to current tenant if provided
        if (request.getUserId() != null) {
            UserEntity user = userRepository.findById(request.getUserId()).orElse(null);
            if (user == null) {
                fieldErrors.add(buildFieldErrorResponse("userId", request.getUserId().toString(), "User not found."));
            } else if (!user.getTenantId().equals(tenantId)) {
                fieldErrors.add(buildFieldErrorResponse("userId", request.getUserId().toString(), "User does not belong to current tenant."));
            }
        }

        // Validate ingredients exist and belong to current tenant if provided
        if (request.getIngredientIds() != null && !request.getIngredientIds().isEmpty()) {
            for (UUID ingredientId : request.getIngredientIds()) {
                IngredientsEntity ingredient = ingredientsRepository.findById(ingredientId).orElse(null);
                if (ingredient == null) {
                    fieldErrors.add(buildFieldErrorResponse("ingredientIds", ingredientId.toString(), "Ingredient not found."));
                } else if (!ingredient.getTenantId().equals(tenantId)) {
                    fieldErrors.add(buildFieldErrorResponse("ingredientIds", ingredientId.toString(), "Ingredient does not belong to current tenant."));
                }
            }
        }

        return fieldErrors;
    }
}
