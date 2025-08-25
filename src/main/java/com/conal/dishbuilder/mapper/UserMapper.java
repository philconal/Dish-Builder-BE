package com.conal.dishbuilder.mapper;

import com.conal.dishbuilder.constant.CommonStatus;
import com.conal.dishbuilder.domain.UserEntity;
import com.conal.dishbuilder.dto.request.RegisterUserRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "avtUrl", target = "logoUrl")
    UserEntity toEntity(RegisterUserRequest request);

    @AfterMapping
    default void afterMapping(@MappingTarget UserEntity.UserEntityBuilder entity) {
        entity.status(CommonStatus.ACTIVE);
    }
}
