package com.conal.dishbuilder.mapper;

import com.conal.dishbuilder.constant.CommonStatus;
import com.conal.dishbuilder.domain.TenantEntity;
import com.conal.dishbuilder.domain.UserEntity;
import com.conal.dishbuilder.dto.request.CreateTenantRequest;
import com.conal.dishbuilder.dto.request.RegisterUserRequest;
import com.conal.dishbuilder.dto.request.UpdateTenantRequest;
import com.conal.dishbuilder.dto.response.TenantResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TenantMapper {
    TenantEntity toEntity(CreateTenantRequest request);

    TenantResponse toDto(TenantEntity tenant);

    @AfterMapping
    default void afterMapping(@MappingTarget TenantEntity.TenantEntityBuilder entity) {
        entity.status(CommonStatus.ACTIVE);
    }

    void updateFromRequest(UpdateTenantRequest request, @MappingTarget TenantEntity existingTenant);
}
