package com.conal.dishbuilder.repository.querydsl;

import com.conal.dishbuilder.dto.request.filter.IngredientsFilterRequest;
import com.conal.dishbuilder.dto.response.IngredientsResponse;
import com.conal.dishbuilder.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface IngredientsQueryDslRepository {
    PageResponse<IngredientsResponse> findAll(IngredientsFilterRequest filter, Pageable pageable);
}
