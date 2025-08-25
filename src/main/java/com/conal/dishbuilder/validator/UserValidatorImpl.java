package com.conal.dishbuilder.validator;

import com.conal.dishbuilder.domain.TenantEntity;
import com.conal.dishbuilder.dto.request.CreateTenantRequest;
import com.conal.dishbuilder.dto.request.RegisterUserRequest;
import com.conal.dishbuilder.dto.request.UpdateTenantRequest;
import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import com.conal.dishbuilder.repository.TenantRepository;
import com.conal.dishbuilder.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserValidatorImpl implements UserValidator {
    private final Validator validator;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    @Override
    public List<FieldErrorResponse> validateCreateAccount(RegisterUserRequest request) {
        List<FieldErrorResponse> fieldErrors = new ArrayList<>();
        Set<ConstraintViolation<Object>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            fieldErrors.addAll(violations.stream()
                    .map(this::buildFieldErrorResponse).toList());
        }
        if(!tenantRepository.existsById(request.getTenantId())){
            fieldErrors.add(buildFieldErrorResponse("tenantId", request.getTenantId().toString(), "TenantId not found."));
        }
        if (userRepository.existsByUsernameAndTenantId(request.getUsername().trim(), request.getTenantId())) {
            fieldErrors.add(buildFieldErrorResponse("username", request.getUsername(), "Username already exists."));
        }
        if (userRepository.existsByEmailAndTenantId(request.getEmail().trim(), request.getTenantId())) {
            fieldErrors.add(buildFieldErrorResponse("email", request.getEmail(), "Email already exists."));
        }

        return fieldErrors;
    }



    private FieldErrorResponse buildFieldErrorResponse(ConstraintViolation<Object> request) {
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
