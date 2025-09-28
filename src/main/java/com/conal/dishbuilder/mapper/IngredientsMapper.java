package com.conal.dishbuilder.mapper;

import com.conal.dishbuilder.domain.IngredientsEntity;
import com.conal.dishbuilder.dto.CreateIngredientsRequest;
import com.conal.dishbuilder.dto.UpdateIngredientsRequest;
import com.conal.dishbuilder.dto.response.IngredientsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IngredientsMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "category", ignore = true)
    IngredientsEntity toEntity(CreateIngredientsRequest request);
    
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    IngredientsResponse toResponse(IngredientsEntity entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntity(UpdateIngredientsRequest request, @MappingTarget IngredientsEntity entity);
}
