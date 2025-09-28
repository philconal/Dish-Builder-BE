package com.conal.dishbuilder.repository.querydsl;

import com.conal.dishbuilder.dto.request.filter.CategoryFilterRequest;
import com.conal.dishbuilder.dto.response.CategoryResponse;
import com.conal.dishbuilder.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CategoryQueryDslRepository {
    PageResponse<CategoryResponse> findAll(CategoryFilterRequest filter, Pageable pageable);
}
