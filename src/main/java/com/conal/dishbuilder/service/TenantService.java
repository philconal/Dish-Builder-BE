package com.conal.dishbuilder.service;

import com.conal.dishbuilder.domain.TenantEntity;
import com.conal.dishbuilder.dto.request.CreateTenantRequest;
import com.conal.dishbuilder.dto.request.TenantFilterRequest;
import com.conal.dishbuilder.dto.request.UpdateTenantRequest;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.dto.response.TenantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TenantService {
    boolean registerTenant(CreateTenantRequest request);

    boolean updateTenant(UpdateTenantRequest request);
    PageResponse<TenantResponse> findAll(TenantFilterRequest filter, Pageable pageable);
    TenantEntity findBySubDomain(String subDomain);
}
