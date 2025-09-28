package com.conal.dishbuilder.repository.querydsl;

import com.conal.dishbuilder.dto.request.filter.DishFilterRequest;
import com.conal.dishbuilder.dto.response.DishResponse;
import com.conal.dishbuilder.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface DishQueryDslRepository {
    PageResponse<DishResponse> findAll(DishFilterRequest filter, Pageable pageable);
}
