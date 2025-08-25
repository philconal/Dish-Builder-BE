package com.conal.dishbuilder.validator;

import com.conal.dishbuilder.domain.TenantEntity;
import com.conal.dishbuilder.dto.request.CreateTenantRequest;
import com.conal.dishbuilder.dto.request.RegisterUserRequest;
import com.conal.dishbuilder.dto.request.UpdateTenantRequest;
import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserValidator {
    List<FieldErrorResponse> validateCreateAccount(RegisterUserRequest request);
}
