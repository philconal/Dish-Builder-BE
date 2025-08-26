package com.conal.dishbuilder.service.impl;

import com.conal.dishbuilder.context.TenantContextHolder;
import com.conal.dishbuilder.domain.UserEntity;
import com.conal.dishbuilder.dto.request.RegisterUserRequest;
import com.conal.dishbuilder.dto.request.UserAccountRequest;
import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import com.conal.dishbuilder.exception.InternalServerException;
import com.conal.dishbuilder.exception.MultipleFieldValidationException;
import com.conal.dishbuilder.mapper.UserMapper;
import com.conal.dishbuilder.repository.UserRepository;
import com.conal.dishbuilder.service.UserService;
import com.conal.dishbuilder.util.PasswordUtils;
import com.conal.dishbuilder.validator.TenantValidator;
import com.conal.dishbuilder.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public boolean registerAccount(RegisterUserRequest request) {
        UUID tenantId = TenantContextHolder.getTenantContext();
        request.setTenantId(tenantId);
        List<FieldErrorResponse> errorResponses = userValidator.validateCreateAccount(request);

        if (!errorResponses.isEmpty()) {
            throw new MultipleFieldValidationException(errorResponses);
        }
        UserEntity userEntity = userMapper.toEntity(request);
        userEntity.setPassword(PasswordUtils.hashPassword(userEntity.getPassword()));
        try {
            userRepository.save(userEntity);
        } catch (Exception e) {
            log.error("Error while saving user: {}", userEntity);
            throw new InternalServerException(e.getMessage());
        }
        return true;
    }
}
