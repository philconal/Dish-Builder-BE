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
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TenantQueryDslRepositoryImpl implements TenantQueryDslRepository {
    private final QTenantEntity tenant = QTenantEntity.tenantEntity;
    private final JPAQueryFactory factory;
    private final TenantMapper tenantMapper;

    @Override
    public PageResponse<TenantResponse> findAll(TenantFilterRequest filter, Pageable pageable) {
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        if (StringUtils.isNotBlank(filter.getName())) {
            conditionBuilder.and(tenant.name.contains(filter.getName().trim()));
        }
        if (StringUtils.isNotBlank(filter.getDomain())) {
            conditionBuilder.and(tenant.subDomain.contains(filter.getDomain().trim()));
        }

        // Case ignore paging
        if (Boolean.TRUE.equals(filter.isIgnorePaging())) {
            var entities = factory.selectFrom(tenant)
                    .where(conditionBuilder)
                    .fetch();

            var responses = entities.stream()
                    .map(tenantMapper::toDto)
                    .toList();

            return PageResponse.fromPage(new PageImpl<>(responses, Pageable.unpaged(), entities.size()));
        }

        // Main query with pagination + sorting
        var entities = factory.selectFrom(tenant)
                .where(conditionBuilder)
                .orderBy(getOrderSpecifiers(pageable).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        var total = factory.select(tenant.count().coalesce(0L)) //Actually in coalesce always return 0L if null no records returned
                .from(tenant)
                .where(conditionBuilder)
                .fetchOne();

        var responses = entities.stream()
                .map(tenantMapper::toDto)
                .toList();

        long safeTotal = total != null ? total : 0L;

        return PageResponse.fromPage(new PageImpl<>(responses, pageable, safeTotal));
    }

    public List<? extends OrderSpecifier<?>> getOrderSpecifiers(Pageable pageable) {
        return pageable.getSort()
                .stream()
                .map(this::toOrderSpecifier)
                .toList();
    }

    private OrderSpecifier<?> toOrderSpecifier(Sort.Order order) {
        Order direction = order.isAscending() ? Order.ASC : Order.DESC;

        return switch (order.getProperty()) {
            case "name" -> new OrderSpecifier<>(direction, tenant.name);
            case "domain" -> new OrderSpecifier<>(direction, tenant.subDomain);
            case "createdAt" -> new OrderSpecifier<>(direction, tenant.createdAt);
            default -> throw new UnsupportedSortPropertyException("Unsupported sort property: " + order.getProperty());
        };
    }

}
