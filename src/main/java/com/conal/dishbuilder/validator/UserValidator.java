package com.conal.dishbuilder.validator;

import com.conal.dishbuilder.domain.UserEntity;
import com.conal.dishbuilder.dto.request.RegisterUserRequest;
import com.conal.dishbuilder.dto.request.UpdateUserRequest;
import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserValidator {
    List<FieldErrorResponse> validateCreateAccount(RegisterUserRequest request);

    List<FieldErrorResponse> validateUpdateUser(UpdateUserRequest request, UserEntity existingUser);
}
