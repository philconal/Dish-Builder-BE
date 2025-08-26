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


@RequiredArgsConstructor
@Slf4j
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

    @Override
    @Transactional
    public boolean updateTenant(UpdateTenantRequest request) {
        TenantEntity tenantEntity = tenantRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException(Constants.Tenant.NOT_FOUND));

        var errorResponses = tenantValidator.validateUpdateTenant(request, tenantEntity);
        if (!errorResponses.isEmpty()) {
            throw new MultipleFieldValidationException(errorResponses);
        }
        TenantEntity newTenantEntity = tenantMapper.toEntity(request);
        newTenantEntity.setSubDomain(tenantEntity.getSubDomain());
        newTenantEntity.setUrlSlug(tenantEntity.getUrlSlug());
        TenantEntity tenant = null;
        try {
            tenant = tenantRepository.save(newTenantEntity);
        } catch (Exception e) {
            log.error("Error while saving tenant: {}", e.getMessage());
            throw new InternalServerException(e.getMessage());
        }
        log.info("Update tenant successfully: {}", tenant);
        return true;
    }

    @Override
    public PageResponse<TenantResponse> findAll(TenantFilterRequest filter, Pageable pageable) {
        return tenantQueryDslRepository.findAll(filter, pageable);
    }

    @Override
    public TenantEntity findBySubDomain(String subDomain) {
        // for local test only
        if (Arrays.asList(environment.getActiveProfiles()).contains("local")) {
            HashMap<String, TenantEntity> hashMap = new HashMap<>();
            hashMap.put("localhost", TenantEntity.builder().id(UUID.fromString("06805dbf-2299-4415-bef3-f53d40b69589")).build());
            hashMap.put("conal1.local", TenantEntity.builder().id(UUID.randomUUID()).build());
            hashMap.put("conal2.local", TenantEntity.builder().id(UUID.randomUUID()).build());
            hashMap.put("conal3.local", TenantEntity.builder().id(UUID.randomUUID()).build());
            return hashMap.get(subDomain);
        }

        return tenantRepository.findBySubDomain(subDomain)
                .orElseThrow(() -> new NotFoundException(Constants.Tenant.NOT_FOUND));
    }
}
