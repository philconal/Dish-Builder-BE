package com.conal.dishbuilder.repository.impl;

import com.conal.dishbuilder.constant.CommonStatus;
import com.conal.dishbuilder.domain.QUserEntity;
import com.conal.dishbuilder.domain.UserEntity;
import com.conal.dishbuilder.dto.request.filter.UserFilterRequest;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.dto.response.UserResponse;
import com.conal.dishbuilder.exception.UnsupportedSortPropertyException;
import com.conal.dishbuilder.mapper.UserMapper;
import com.conal.dishbuilder.repository.querydsl.UserQueryDslRepository;
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
public class UserQueryDslRepositoryImpl implements UserQueryDslRepository {

    private final QUserEntity user = QUserEntity.userEntity;
    private final JPAQueryFactory factory;
    private final UserMapper userMapper;

    @Override
    public PageResponse<UserResponse> findAll(UserFilterRequest filter, Pageable pageable) {
        BooleanBuilder conditionBuilder = new BooleanBuilder();

        log.debug("Filtering users with filter: {}", filter);

        if (StringUtils.isNotBlank(filter.getUsername())) {
            conditionBuilder.and(user.username.containsIgnoreCase(filter.getUsername().trim()));
        }
        if (StringUtils.isNotBlank(filter.getEmail())) {
            conditionBuilder.and(user.email.containsIgnoreCase(filter.getEmail().trim()));
        }
        if (StringUtils.isNotBlank(filter.getPhone())) {
            conditionBuilder.and(user.phone.eq(filter.getPhone().trim()));
        }
        if (filter.getStatus() != null) {
            conditionBuilder.and(user.status.eq(filter.getStatus()));
        }

        if (StringUtils.isNotBlank(filter.getFullName())) {
            conditionBuilder.and(user.firstName.concat(" ").concat(user.lastName)
                    .containsIgnoreCase(filter.getFullName().trim()));
        }
        if (filter.getTenantId() != null) {
            conditionBuilder.and(user.tenantId.eq(filter.getTenantId()));
        }
        // Ignore pagination
        if (Boolean.TRUE.equals(filter.isIgnorePaging())) {
            log.info("Fetching all users without pagination");

            List<UserEntity> entities = factory.selectFrom(user)
                    .where(conditionBuilder)
                    .fetch();

            List<UserResponse> responses = entities.stream()
                    .map(userMapper::toDto)
                    .toList();

            return PageResponse.fromPage(new PageImpl<>(responses, Pageable.unpaged(), responses.size()));
        }

        log.info("Fetching users with pagination - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        List<OrderSpecifier<?>> orderSpecifiers = getOrderSpecifiers(pageable);
        log.debug("Sorting with: {}", orderSpecifiers);

        List<UserEntity> pagedEntities = factory.selectFrom(user)
                .where(conditionBuilder)
                .orderBy(orderSpecifiers.toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = factory.select(user.count().coalesce(0L))
                .from(user)
                .where(conditionBuilder)
                .fetchOne();

        long safeTotal = totalCount != null ? totalCount : 0L;

        List<UserResponse> responses = pagedEntities.stream()
                .map(userMapper::toDto)
                .toList();

        log.info("Fetched {} users out of total {}", responses.size(), safeTotal);

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
            case "username" -> new OrderSpecifier<>(direction, user.username);
            case "fullName" -> new OrderSpecifier<>(direction, user.firstName.concat(user.lastName));
            case "email" -> new OrderSpecifier<>(direction, user.email);
            case "phone" -> new OrderSpecifier<>(direction, user.phone);
            case "status" -> new OrderSpecifier<>(direction, user.status);
            case "createdAt" -> new OrderSpecifier<>(direction, user.createdAt);
            default -> {
                log.error("Unsupported sort property: {}", order.getProperty());
                throw new UnsupportedSortPropertyException("Unsupported sort property: " + order.getProperty());
            }
        };
    }
}
