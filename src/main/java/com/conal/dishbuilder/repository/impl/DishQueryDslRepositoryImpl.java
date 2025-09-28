package com.conal.dishbuilder.repository.impl;

import com.conal.dishbuilder.domain.DishEntity;
import com.conal.dishbuilder.dto.request.filter.DishFilterRequest;
import com.conal.dishbuilder.dto.response.DishResponse;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.mapper.DishMapper;
import com.conal.dishbuilder.repository.querydsl.DishQueryDslRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.conal.dishbuilder.domain.QDishEntity.dishEntity;
import static com.conal.dishbuilder.domain.QUserEntity.userEntity;
import static com.conal.dishbuilder.domain.QIngredientsEntity.ingredientsEntity;

@Repository
@RequiredArgsConstructor
public class DishQueryDslRepositoryImpl implements DishQueryDslRepository {
    private final JPAQueryFactory queryFactory;
    private final DishMapper dishMapper;

    @Override
    public PageResponse<DishResponse> findAll(DishFilterRequest filter, Pageable pageable) {
        BooleanBuilder whereClause = buildWhereClause(filter);
        
        // Get total count
        long total = queryFactory
                .select(dishEntity.count())
                .from(dishEntity)
                .leftJoin(dishEntity.user, userEntity)
                .leftJoin(dishEntity.ingredients, ingredientsEntity)
                .where(whereClause)
                .fetchOne();
        
        // Get paginated results with joins
        List<DishEntity> entities = queryFactory
                .selectFrom(dishEntity)
                .leftJoin(dishEntity.user, userEntity)
                .fetchJoin()
                .leftJoin(dishEntity.ingredients, ingredientsEntity)
                .fetchJoin()
                .where(whereClause)
                .orderBy(dishEntity.name.asc()) // Default ordering
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        
        // Convert to response DTOs
        List<DishResponse> responses = entities.stream()
                .map(dishMapper::toResponse)
                .toList();
        
        // Create page
        Page<DishResponse> page = new PageImpl<>(responses, pageable, total);
        
        return PageResponse.fromPage(page);
    }
    
    private BooleanBuilder buildWhereClause(DishFilterRequest filter) {
        BooleanBuilder builder = new BooleanBuilder();
        
        // Filter by tenant
        if (filter.getTenantId() != null) {
            builder.and(dishEntity.tenantId.eq(filter.getTenantId()));
        }
        
        // Filter by name
        if (StringUtils.hasText(filter.getName())) {
            builder.and(dishEntity.name.containsIgnoreCase(filter.getName()));
        }
        
        // Filter by description
        if (StringUtils.hasText(filter.getDescription())) {
            builder.and(dishEntity.description.containsIgnoreCase(filter.getDescription()));
        }
        
        // Filter by total price range
        if (filter.getMinTotalPrice() != null) {
            builder.and(dishEntity.totalPrice.goe(filter.getMinTotalPrice()));
        }
        if (filter.getMaxTotalPrice() != null) {
            builder.and(dishEntity.totalPrice.loe(filter.getMaxTotalPrice()));
        }
        
        // Filter by discount range
        if (filter.getMinDiscount() != null) {
            builder.and(dishEntity.discount.goe(filter.getMinDiscount()));
        }
        if (filter.getMaxDiscount() != null) {
            builder.and(dishEntity.discount.loe(filter.getMaxDiscount()));
        }
        
        // Filter by user ID
        if (filter.getUserId() != null) {
            builder.and(dishEntity.user.id.eq(filter.getUserId()));
        }
        
        // Filter by user name
        if (StringUtils.hasText(filter.getUserName())) {
            builder.and(userEntity.username.containsIgnoreCase(filter.getUserName()));
        }
        
        // Filter by ingredient ID
        if (filter.getIngredientId() != null) {
            builder.and(ingredientsEntity.id.eq(filter.getIngredientId()));
        }
        
        // Filter by ingredient name
        if (StringUtils.hasText(filter.getIngredientName())) {
            builder.and(ingredientsEntity.name.containsIgnoreCase(filter.getIngredientName()));
        }
        
        return builder;
    }
}
