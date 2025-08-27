package com.conal.dishbuilder.service.impl;

import com.conal.dishbuilder.constant.Constants;
import com.conal.dishbuilder.domain.TenantEntity;
import com.conal.dishbuilder.dto.request.CreateTenantRequest;
import com.conal.dishbuilder.dto.request.TenantFilterRequest;
import com.conal.dishbuilder.dto.request.UpdateTenantRequest;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.dto.response.TenantResponse;
import com.conal.dishbuilder.exception.InternalServerException;
import com.conal.dishbuilder.exception.MultipleFieldValidationException;
import com.conal.dishbuilder.exception.NotFoundException;
import com.conal.dishbuilder.mapper.TenantMapper;
import com.conal.dishbuilder.repository.TenantRepository;
import com.conal.dishbuilder.repository.querydsl.TenantQueryDslRepository;
import com.conal.dishbuilder.service.TenantService;
import com.conal.dishbuilder.validator.TenantValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TenantServiceImpl implements TenantService {

    private final TenantValidator tenantValidator;
    private final TenantMapper tenantMapper;
    private final TenantRepository tenantRepository;
    private final TenantQueryDslRepository tenantQueryDslRepository;
    private final Environment environment;

    @Override
    @Transactional
    public boolean registerTenant(CreateTenantRequest request) {
        log.info("Registering new tenant with name: {}", request.getName());

        var errorResponses = tenantValidator.validateCreateTenant(request);
        if (!errorResponses.isEmpty()) {
            log.warn("Tenant registration failed due to validation errors: {}", errorResponses);
            throw new MultipleFieldValidationException(errorResponses);
        }

        TenantEntity tenantEntity = tenantMapper.toEntity(request);

        try {
            tenantRepository.save(tenantEntity);
            log.info("Tenant saved successfully: {}", tenantEntity.getId());
            return true;
        } catch (Exception e) {
            log.error("Error while saving tenant: {}", e.getMessage(), e);
            throw new InternalServerException("Unable to register tenant");
        }
    }

    @Override
    @Transactional
    public boolean updateTenant(UpdateTenantRequest request) {
        log.info("Updating tenant with ID: {}", request.getId());

        TenantEntity existingTenant = tenantRepository.findById(request.getId())
                .orElseThrow(() -> {
                    log.warn("Tenant not found with ID: {}", request.getId());
                    return new NotFoundException(Constants.Tenant.NOT_FOUND);
                });

        var errorResponses = tenantValidator.validateUpdateTenant(request, existingTenant);
        if (!errorResponses.isEmpty()) {
            log.warn("Tenant update validation failed: {}", errorResponses);
            throw new MultipleFieldValidationException(errorResponses);
        }

        TenantEntity updatedTenant = tenantMapper.toEntity(request);
        updatedTenant.setSubDomain(existingTenant.getSubDomain()); // Keep original values
        updatedTenant.setUrlSlug(existingTenant.getUrlSlug());

        try {
            tenantRepository.save(updatedTenant);
            log.info("Tenant updated successfully: {}", updatedTenant.getId());
            return true;
        } catch (Exception e) {
            log.error("Error while updating tenant: {}", e.getMessage(), e);
            throw new InternalServerException("Failed to update tenant");
        }
    }

    @Override
    public PageResponse<TenantResponse> findAll(TenantFilterRequest filter, Pageable pageable) {
        log.info("Fetching tenants with filter: {}", filter);
        PageResponse<TenantResponse> response = tenantQueryDslRepository.findAll(filter, pageable);
        log.info("Fetched {} tenants", response.getData().size());
        return response;
    }

    @Override
    public TenantEntity findBySubDomain(String subDomain) {
        log.info("Looking up tenant by subdomain: {}", subDomain);

        if (Arrays.asList(environment.getActiveProfiles()).contains("local")) {
            log.debug("Using local mock tenant map for subdomain: {}", subDomain);

            HashMap<String, TenantEntity> mockTenants = new HashMap<>();
            mockTenants.put("localhost", TenantEntity.builder().id(UUID.fromString("06805dbf-2299-4415-bef3-f53d40b69589")).build());
            mockTenants.put("conal1.local", TenantEntity.builder().id(UUID.randomUUID()).build());
            mockTenants.put("conal2.local", TenantEntity.builder().id(UUID.randomUUID()).build());
            mockTenants.put("conal3.local", TenantEntity.builder().id(UUID.randomUUID()).build());

            TenantEntity localTenant = mockTenants.get(subDomain);
            if (localTenant == null) {
                log.warn("Local mock tenant not found for subdomain: {}", subDomain);
                throw new NotFoundException(Constants.Tenant.NOT_FOUND);
            }
            return localTenant;
        }

        return tenantRepository.findBySubDomain(subDomain)
                .orElseThrow(() -> {
                    log.warn("Tenant not found for subdomain: {}", subDomain);
                    return new NotFoundException(Constants.Tenant.NOT_FOUND);
                });
    }
}
