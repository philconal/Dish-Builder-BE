package com.conal.dishbuilder.service;

import com.conal.dishbuilder.constant.CommonStatus;
import com.conal.dishbuilder.domain.TenantEntity;
import com.conal.dishbuilder.dto.request.CreateTenantRequest;
import com.conal.dishbuilder.dto.request.filter.TenantFilterRequest;
import com.conal.dishbuilder.dto.request.UpdateTenantRequest;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.dto.response.TenantResponse;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;

public interface TenantService {
    boolean registerTenant(CreateTenantRequest request);

    boolean updateTenant(UpdateTenantRequest request);
    PageResponse<TenantResponse> findAll(TenantFilterRequest filter, Pageable pageable);
    TenantEntity findBySubDomain(String subDomain);
    TenantEntity findDefaultTenant();
}
