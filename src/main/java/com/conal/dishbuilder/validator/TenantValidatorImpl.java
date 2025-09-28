package com.conal.dishbuilder.validator;

import com.conal.dishbuilder.domain.TenantEntity;
import com.conal.dishbuilder.dto.request.CreateTenantRequest;
import com.conal.dishbuilder.dto.request.UpdateTenantRequest;
import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import com.conal.dishbuilder.repository.TenantRepository;
import com.conal.dishbuilder.util.CommonUtils;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.conal.dishbuilder.util.CommonUtils.buildFieldErrorResponse;

@Component
@RequiredArgsConstructor
public class TenantValidatorImpl implements TenantValidator {
    private final Validator validator;
    private final TenantRepository tenantRepository;

    @Override
    public List<FieldErrorResponse> validateCreateTenant(CreateTenantRequest request) {
        Set<ConstraintViolation<Object>> violations = validator.validate(request);
        var fieldErrors = violations.stream()
                .map(CommonUtils::buildFieldErrorResponse).collect(Collectors.toList());

        if (tenantRepository.existsByUrlSlug(request.getUrlSlug())) {
            fieldErrors.add(buildFieldErrorResponse("urlSlug", request.getUrlSlug(), "Url Slug already exists."));
        }
        if (tenantRepository.existsByEmail(request.getEmail())) {
            fieldErrors.add(buildFieldErrorResponse("email", request.getEmail(), "Email already exists."));
        }
        if (tenantRepository.existsByName(request.getName())) {
            fieldErrors.add(buildFieldErrorResponse("name", request.getName(), "Name already exists."));
        }
        return fieldErrors;
    }

    @Override
    public List<FieldErrorResponse> validateUpdateTenant(UpdateTenantRequest request, TenantEntity tenantEntity) {
        Set<ConstraintViolation<Object>> violations = validator.validate(request);
        var fieldErrors = violations.stream()
                .map(CommonUtils::buildFieldErrorResponse).collect(Collectors.toList());
        if (!tenantEntity.getEmail().equals(request.getEmail()) && tenantRepository.existsByEmail(request.getEmail())) {
            fieldErrors.add(buildFieldErrorResponse("email", request.getEmail(), "Email already exists."));
        }
        if (!tenantEntity.getName().equals(request.getName()) && tenantRepository.existsByName(request.getName())) {
            fieldErrors.add(buildFieldErrorResponse("name", request.getName(), "Name already exists."));
        }
        return fieldErrors;
    }
}
