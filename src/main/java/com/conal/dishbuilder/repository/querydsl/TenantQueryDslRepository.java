package com.conal.dishbuilder.repository.querydsl;

import com.conal.dishbuilder.dto.request.filter.TenantFilterRequest;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.dto.response.TenantResponse;
import org.springframework.data.domain.Pageable;

public interface TenantQueryDslRepository {
    PageResponse<TenantResponse> findAll(TenantFilterRequest filter, Pageable pageable);
}
