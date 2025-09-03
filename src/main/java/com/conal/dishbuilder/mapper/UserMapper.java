package com.conal.dishbuilder.mapper;

import com.conal.dishbuilder.constant.CommonStatus;
import com.conal.dishbuilder.constant.UserType;
import com.conal.dishbuilder.domain.UserEntity;
import com.conal.dishbuilder.dto.request.RegisterUserRequest;
import com.conal.dishbuilder.dto.request.UpdateUserRequest;
import com.conal.dishbuilder.dto.response.UserResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    @Mapping(source = "avtUrl", target = "logoUrl")
    UserEntity toEntity(RegisterUserRequest request);

    void updateFromRequest(UpdateUserRequest request, @MappingTarget UserEntity entity);

    @Mapping(source = "tenant.name", target = "tenant")
    UserResponse toDto(UserEntity request);

    @AfterMapping
    default void afterMapping(@MappingTarget UserEntity.UserEntityBuilder entity) {
        entity.status(CommonStatus.ACTIVE);
        entity.userType(UserType.CUSTOMER);
    }
}
