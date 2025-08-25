package com.conal.dishbuilder.mapper;

import com.conal.dishbuilder.constant.CommonStatus;
import com.conal.dishbuilder.domain.TenantEntity;
import com.conal.dishbuilder.domain.UserEntity;
import com.conal.dishbuilder.dto.request.CreateTenantRequest;
import com.conal.dishbuilder.dto.request.RegisterUserRequest;
import com.conal.dishbuilder.dto.request.UpdateTenantRequest;
import com.conal.dishbuilder.dto.response.TenantResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TenantMapper {
    TenantEntity toEntity(CreateTenantRequest request);
    TenantEntity toEntity(UpdateTenantRequest request);
    TenantResponse toDto(TenantEntity tenant);

    @AfterMapping
    default void afterMapping( @MappingTarget TenantEntity.TenantEntityBuilder  entity) {
        entity.status(CommonStatus.ACTIVE);
    }
}
