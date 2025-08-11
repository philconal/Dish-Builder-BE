package com.conal.dishbuilder.validator;

import com.conal.dishbuilder.dto.request.CreateTenantRequest;
import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import com.conal.dishbuilder.repository.TenantRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TenantValidatorImpl implements TenantValidator {
    private final Validator validator;
    private final TenantRepository tenantRepository;

    @Override
    public List<FieldErrorResponse> validateCreateTenant(CreateTenantRequest request) {
        List<FieldErrorResponse> fieldErrors = new ArrayList<>();
        Set<ConstraintViolation<CreateTenantRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            fieldErrors.addAll(violations.stream()
                    .map(this::buildFieldErrorResponse).toList());
        }
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

    private FieldErrorResponse buildFieldErrorResponse(ConstraintViolation<CreateTenantRequest> request) {
        return FieldErrorResponse.builder()
                .setField(request.getPropertyPath().toString()) // tên field bị lỗi
                .setRejectedValue(request.getInvalidValue() != null ? request.getInvalidValue().toString() : null) // giá trị bị reject
                .setMessage(request.getMessage()) // message từ @NotBlank, @Email,...
                .build();
    }

    private FieldErrorResponse buildFieldErrorResponse(String field, String rejectedValue, String message) {
        return FieldErrorResponse.builder()
                .setMessage(message)
                .setRejectedValue(rejectedValue)
                .setField(field)
                .build();
    }
}
