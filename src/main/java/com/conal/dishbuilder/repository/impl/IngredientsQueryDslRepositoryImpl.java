package com.conal.dishbuilder.repository.impl;

import com.conal.dishbuilder.domain.IngredientsEntity;
import com.conal.dishbuilder.dto.request.filter.IngredientsFilterRequest;
import com.conal.dishbuilder.dto.response.IngredientsResponse;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.mapper.IngredientsMapper;
import com.conal.dishbuilder.repository.querydsl.IngredientsQueryDslRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.conal.dishbuilder.domain.QIngredientsEntity.ingredientsEntity;
import static com.conal.dishbuilder.domain.QCategoryEntity.categoryEntity;

@Repository
@RequiredArgsConstructor
public class IngredientsQueryDslRepositoryImpl implements IngredientsQueryDslRepository {
    private final JPAQueryFactory queryFactory;
    private final IngredientsMapper ingredientsMapper;

    @Override
    public PageResponse<IngredientsResponse> findAll(IngredientsFilterRequest filter, Pageable pageable) {
        BooleanBuilder whereClause = buildWhereClause(filter);
        
        // Get total count
        long total = queryFactory
                .select(ingredientsEntity.count())
                .from(ingredientsEntity)
                .leftJoin(ingredientsEntity.category, categoryEntity)
                .where(whereClause)
                .fetchOne();
        
        // Get paginated results with category join
        List<IngredientsEntity> entities = queryFactory
                .selectFrom(ingredientsEntity)
                .leftJoin(ingredientsEntity.category, categoryEntity)
                .fetchJoin()
                .where(whereClause)
                .orderBy(ingredientsEntity.name.asc()) // Default ordering
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        // Convert to response DTOs
        List<IngredientsResponse> responses = entities.stream()
                .map(ingredientsMapper::toResponse)
                .toList();
        
        // Create page
        Page<IngredientsResponse> page = new PageImpl<>(responses, pageable, total);
        
        return PageResponse.fromPage(page);
    }
    
    private BooleanBuilder buildWhereClause(IngredientsFilterRequest filter) {
        BooleanBuilder builder = new BooleanBuilder();
        
        // Filter by tenant
        if (filter.getTenantId() != null) {
            builder.and(ingredientsEntity.tenantId.eq(filter.getTenantId()));
        }
        
        // Filter by name
        if (StringUtils.hasText(filter.getName())) {
            builder.and(ingredientsEntity.name.containsIgnoreCase(filter.getName()));
        }
        
        // Filter by description
        if (StringUtils.hasText(filter.getDescription())) {
            builder.and(ingredientsEntity.description.containsIgnoreCase(filter.getDescription()));
        }
        
        // Filter by price range
        if (filter.getMinPrice() != null) {
            builder.and(ingredientsEntity.price.goe(filter.getMinPrice()));
        }
        if (filter.getMaxPrice() != null) {
            builder.and(ingredientsEntity.price.loe(filter.getMaxPrice()));
        }
        
        // Filter by category ID
        if (filter.getCategoryId() != null) {
            builder.and(ingredientsEntity.category.id.eq(filter.getCategoryId()));
        }
        
        // Filter by category name
        if (StringUtils.hasText(filter.getCategoryName())) {
            builder.and(categoryEntity.name.containsIgnoreCase(filter.getCategoryName()));
        }
        
        return builder;
    }
}
