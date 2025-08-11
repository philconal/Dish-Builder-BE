package com.conal.dishbuilder.mapper;

import com.conal.dishbuilder.domain.TenantEntity;
import com.conal.dishbuilder.dto.request.CreateTenantRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TenantMapper {
    @Mapping(source = "urlSlug", target = "urlSlug")
    TenantEntity toEntity(CreateTenantRequest request);
}
