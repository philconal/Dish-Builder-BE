package com.conal.dishbuilder.repository.querydsl;

import com.conal.dishbuilder.dto.request.filter.TenantFilterRequest;
import com.conal.dishbuilder.dto.request.filter.UserFilterRequest;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.dto.response.UserResponse;
import org.springframework.data.domain.Pageable;

public interface UserQueryDslRepository {
    PageResponse<UserResponse> findAll(UserFilterRequest filter, Pageable pageable);
}
