package com.conal.dishbuilder.validator;

import com.conal.dishbuilder.dto.request.CreateTenantRequest;
import com.conal.dishbuilder.dto.response.FieldErrorResponse;

import java.util.List;

public interface TenantValidator {
    List<FieldErrorResponse> validateCreateTenant(CreateTenantRequest request);
}
