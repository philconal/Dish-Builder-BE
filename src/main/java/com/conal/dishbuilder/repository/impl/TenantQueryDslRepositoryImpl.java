package com.conal.dishbuilder.repository.impl;

import com.conal.dishbuilder.domain.QTenantEntity;
import com.conal.dishbuilder.domain.TenantEntity;
import com.conal.dishbuilder.dto.request.TenantFilterRequest;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.dto.response.TenantResponse;
import com.conal.dishbuilder.exception.UnsupportedSortPropertyException;
import com.conal.dishbuilder.mapper.TenantMapper;
import com.conal.dishbuilder.repository.querydsl.TenantQueryDslRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TenantQueryDslRepositoryImpl implements TenantQueryDslRepository {

    private final QTenantEntity tenant = QTenantEntity.tenantEntity;
    private final JPAQueryFactory factory;
    private final TenantMapper tenantMapper;

    @Override
    public PageResponse<TenantResponse> findAll(TenantFilterRequest filter, Pageable pageable) {
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        log.debug("Filtering tenants with filter: {}", filter);

        if (StringUtils.isNotBlank(filter.getName())) {
            conditionBuilder.and(tenant.name.containsIgnoreCase(filter.getName().trim()));
        }

        if (StringUtils.isNotBlank(filter.getDomain())) {
            conditionBuilder.and(tenant.subDomain.containsIgnoreCase(filter.getDomain().trim()));
        }

        // Ignore pagination
        if (Boolean.TRUE.equals(filter.isIgnorePaging())) {
            log.info("Fetching all tenants without pagination");

            List<TenantEntity> entities = factory.selectFrom(tenant)
                    .where(conditionBuilder)
                    .fetch();

            List<TenantResponse> responses = entities.stream()
                    .map(tenantMapper::toDto)
                    .toList();

            return PageResponse.fromPage(new PageImpl<>(responses, Pageable.unpaged(), responses.size()));
        }

        log.info("Fetching tenants with pagination - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(pageable);
        log.debug("Sorting with: {}", orderSpecifiers);

        List<TenantEntity> pagedEntities = factory.selectFrom(tenant)
                .where(conditionBuilder)
                .orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = factory.select(tenant.count().coalesce(0L))
                .from(tenant)
                .where(conditionBuilder)
                .fetchOne();

        long safeTotal = totalCount != null ? totalCount : 0L;

        List<TenantResponse> responses = pagedEntities.stream()
                .map(tenantMapper::toDto)
                .toList();

        log.info("Fetched {} tenants out of total {}", responses.size(), safeTotal);

        return PageResponse.fromPage(new PageImpl<>(responses, pageable, safeTotal));
    }

    public List<OrderSpecifier<?>> getOrderSpecifiers(Pageable pageable) {
        return pageable.getSort()
                .stream()
                .map(this::toOrderSpecifier)
                .collect(Collectors.toList());
    }

    private OrderSpecifier<?> toOrderSpecifier(Sort.Order order) {
        Order direction = order.isAscending() ? Order.ASC : Order.DESC;

        log.debug("Creating order specifier - property: {}, direction: {}", order.getProperty(), direction);

        return switch (order.getProperty()) {
            case "name" -> new OrderSpecifier<>(direction, tenant.name);
            case "domain" -> new OrderSpecifier<>(direction, tenant.subDomain);
            case "createdAt" -> new OrderSpecifier<>(direction, tenant.createdAt);
            default -> {
                log.error("Unsupported sort property: {}", order.getProperty());
                throw new UnsupportedSortPropertyException("Unsupported sort property: " + order.getProperty());
            }
        };
    }
}
