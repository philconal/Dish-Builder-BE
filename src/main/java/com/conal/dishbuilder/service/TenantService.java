package com.conal.dishbuilder.service;

import com.conal.dishbuilder.dto.request.CreateTenantRequest;

public interface TenantService {
    boolean registerTenant(CreateTenantRequest request);

}
