package com.conal.dishbuilder.repository.impl;

import com.conal.dishbuilder.domain.CategoryEntity;
import com.conal.dishbuilder.dto.request.filter.CategoryFilterRequest;
import com.conal.dishbuilder.dto.response.CategoryResponse;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.mapper.CategoryMapper;
import com.conal.dishbuilder.repository.querydsl.CategoryQueryDslRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.conal.dishbuilder.domain.QCategoryEntity.categoryEntity;

@Repository
@RequiredArgsConstructor
public class CategoryQueryDslRepositoryImpl implements CategoryQueryDslRepository {
    private final JPAQueryFactory queryFactory;
    private final CategoryMapper categoryMapper;

    @Override
    public PageResponse<CategoryResponse> findAll(CategoryFilterRequest filter, Pageable pageable) {
        BooleanBuilder whereClause = buildWhereClause(filter);
        
        // Get total count
        long total = queryFactory
                .select(categoryEntity.count())
                .from(categoryEntity)
                .where(whereClause)
                .fetchOne();
        
        // Get paginated results
        List<CategoryEntity> entities = queryFactory
                .selectFrom(categoryEntity)
                .where(whereClause)
                .orderBy(categoryEntity.name.asc()) // Default ordering
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        // Convert to response DTOs
        List<CategoryResponse> responses = entities.stream()
                .map(categoryMapper::toResponse)
                .toList();
        
        // Create page
        Page<CategoryResponse> page = new PageImpl<>(responses, pageable, total);
        
        return PageResponse.fromPage(page);
    }
    
    private BooleanBuilder buildWhereClause(CategoryFilterRequest filter) {
        BooleanBuilder builder = new BooleanBuilder();
        
        // Filter by tenant
        if (filter.getTenantId() != null) {
            builder.and(categoryEntity.tenantId.eq(filter.getTenantId()));
        }
        
        // Filter by name
        if (StringUtils.hasText(filter.getName())) {
            builder.and(categoryEntity.name.containsIgnoreCase(filter.getName()));
        }
        
        // Filter by description
        if (StringUtils.hasText(filter.getDescription())) {
            builder.and(categoryEntity.description.containsIgnoreCase(filter.getDescription()));
        }
        
        return builder;
    }
}
