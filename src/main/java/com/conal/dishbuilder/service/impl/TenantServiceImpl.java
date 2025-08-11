package com.conal.dishbuilder.service.impl;

import com.conal.dishbuilder.domain.TenantEntity;
import com.conal.dishbuilder.dto.request.CreateTenantRequest;
import com.conal.dishbuilder.exception.InternalServerException;
import com.conal.dishbuilder.exception.MultipleFieldValidationException;
import com.conal.dishbuilder.mapper.TenantMapper;
import com.conal.dishbuilder.repository.TenantRepository;
import com.conal.dishbuilder.service.TenantService;
import com.conal.dishbuilder.validator.TenantValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class TenantServiceImpl implements TenantService {
    private final TenantValidator tenantValidator;
    private final TenantMapper tenantMapper;
    private final TenantRepository tenantRepository;

    @Override
    public boolean registerTenant(CreateTenantRequest request) {
        var errorResponses = tenantValidator.validateCreateTenant(request);
        if (!errorResponses.isEmpty()) {
            throw new MultipleFieldValidationException(errorResponses);
        }
        TenantEntity tenantEntity = tenantMapper.toEntity(request);
        try {
            tenantRepository.save(tenantEntity);
        } catch (Exception e) {
            log.error("Error while saving tenant: {}", e.getMessage());
            throw new InternalServerException(e.getMessage());
        }
        return true;
    }
}
