package com.conal.dishbuilder.service.impl;

import com.conal.dishbuilder.constant.CommonStatus;
import com.conal.dishbuilder.constant.Constants;
import com.conal.dishbuilder.context.TenantContextHolder;
import com.conal.dishbuilder.domain.TenantEntity;
import com.conal.dishbuilder.domain.RoleEntity;
import com.conal.dishbuilder.domain.UserEntity;
import com.conal.dishbuilder.dto.request.RegisterUserRequest;
import com.conal.dishbuilder.dto.request.UpdateUserRequest;
import com.conal.dishbuilder.dto.request.filter.UserFilterRequest;
import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.dto.response.UserResponse;
import com.conal.dishbuilder.dto.response.UserInfoResponse;
import com.conal.dishbuilder.context.UserContextHolder;
import com.conal.dishbuilder.constant.UserType;
import com.conal.dishbuilder.exception.InternalServerException;
import com.conal.dishbuilder.exception.MultipleFieldValidationException;
import com.conal.dishbuilder.exception.NotFoundException;
import com.conal.dishbuilder.mapper.UserMapper;
import com.conal.dishbuilder.repository.TenantRepository;
import com.conal.dishbuilder.repository.RoleRepository;
import com.conal.dishbuilder.repository.UserRepository;
import com.conal.dishbuilder.repository.querydsl.UserQueryDslRepository;
import com.conal.dishbuilder.service.UserService;
import com.conal.dishbuilder.util.PasswordUtils;
import com.conal.dishbuilder.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserQueryDslRepository queryDslRepository;
    private final TenantRepository tenantRepository;
    private final RoleRepository roleRepository;

    @Override
    public boolean registerAccount(RegisterUserRequest request) {
        UUID tenantId = TenantContextHolder.getTenantContext();
        request.setTenantId(tenantId);

        log.info("Registering new user under tenantId: {}", tenantId);

        List<FieldErrorResponse> errorResponses = userValidator.validateCreateAccount(request);
        if (!errorResponses.isEmpty()) {
            log.warn("User registration failed validation: {}", errorResponses);
            throw new MultipleFieldValidationException(errorResponses);
        }

        UserEntity userEntity = userMapper.toEntity(request);
        userEntity.setPassword(PasswordUtils.hashPassword(userEntity.getPassword()));
        TenantEntity tenantEntity = tenantRepository.findById(tenantId).orElseThrow(
                () -> new NotFoundException("Tenant not found with id: " + tenantId)
        );
        if (tenantEntity.getStatus().equals(CommonStatus.DEFAULT)) {
            userEntity.setStatus(CommonStatus.ACTIVE);
        }

        RoleEntity customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new NotFoundException("Default role CUSTOMER not found"));
        if (userEntity.getRoles() == null) {
            userEntity.setRoles(new HashSet<>());
        } else {
            userEntity.getRoles().clear();
        }
        userEntity.getRoles().add(customerRole);

        try {
            userRepository.save(userEntity);
            log.info("User registered successfully: {}", userEntity.getEmail());
        } catch (Exception e) {
            log.error("Error while saving user: {}", userEntity.getEmail(), e);
            throw new InternalServerException("Failed to save user: " + e.getMessage());
        }

        return true;
    }

    @Override
    public PageResponse<UserResponse> findAllUsers(UserFilterRequest request, Pageable pageable) {
        // Get current user to check their role
        String currentUsername = UserContextHolder.getUserContext();
        if (currentUsername != null) {
            UserEntity currentUser = userRepository.findByUsernameAndStatus(currentUsername, CommonStatus.ACTIVE)
                    .orElse(null);
            
            if (currentUser != null) {
                // Check if user has SUPER_ADMIN role
                boolean isSuperAdmin = currentUser.getRoles().stream()
                        .anyMatch(role -> "SUPER_ADMIN".equals(role.getName()));
                
                // If not SUPER_ADMIN, filter by current user's tenant
                if (!isSuperAdmin) {
                    log.info("Non-super admin user '{}' accessing users, filtering by tenant: {}", 
                            currentUsername, currentUser.getTenantId());
                    // Set tenant filter to current user's tenant
                    request.setTenantId(currentUser.getTenantId());
                }
            }
        }
        
        return queryDslRepository.findAll(request, pageable);
    }

        @Override
    public boolean updateUserProfile(UpdateUserRequest request) {
        UUID tenantId = TenantContextHolder.getTenantContext();
        log.info("Updating user with ID: {}", request.getId());

        // Get current user to check their role
        String currentUsername = UserContextHolder.getUserContext();
        UserEntity currentUser = null;
        if (currentUsername != null) {
            currentUser = userRepository.findByUsernameAndStatus(currentUsername, CommonStatus.ACTIVE)
                    .orElse(null);
        }

        UserEntity existingUser = userRepository.findByIdAndTenantId(request.getId(), tenantId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", request.getId());
                    return new NotFoundException("User not found");
                });

        // Check if current user is SUPER_ADMIN or if they're updating their own profile
        if (currentUser != null) {
            boolean isSuperAdmin = currentUser.getRoles().stream()
                    .anyMatch(role -> "SUPER_ADMIN".equals(role.getName()));
            
            // If not SUPER_ADMIN, ensure they can only update users from their own tenant
            if (!isSuperAdmin && !currentUser.getTenantId().equals(existingUser.getTenantId())) {
                log.warn("User '{}' attempted to update user from different tenant", currentUsername);
                throw new NotFoundException("User not found");
            }
        }

        var errorResponses = userValidator.validateUpdateUser(request, existingUser);
        if (!errorResponses.isEmpty()) {
            log.warn("User update validation failed: {}", errorResponses);
            throw new MultipleFieldValidationException(errorResponses);
        }
        userMapper.updateFromRequest(request, existingUser);
        try {
            userRepository.save(existingUser);
            log.info("User updated successfully: {}", existingUser.getId());
            return true;
        } catch (Exception e) {
            log.error("Error while updating user: {}", e.getMessage(), e);
            throw new InternalServerException("Failed to update user");
        }
    }

    @Override
    public UserInfoResponse getCurrentUserInfo() {
        String username = UserContextHolder.getUserContext();
        if (username == null) {
            throw new NotFoundException("User context not found");
        }

        UUID tenantId = TenantContextHolder.getTenantContext();
        UserEntity user = userRepository.findByUsernameAndTenantIdAndStatus(username, tenantId, CommonStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<String> roles = user.getRoles().stream()
                .map(RoleEntity::getName)
                .toList();

        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .logoUrl(user.getLogoUrl())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .registerWith(user.getRegisterWith())
                .status(user.getStatus())
                .userType(user.getUserType())
                .tenant(user.getTenant() != null ? user.getTenant().getName() : null)
                .roles(roles)
                .build();
    }
}
