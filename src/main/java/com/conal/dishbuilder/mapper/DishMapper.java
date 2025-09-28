package com.conal.dishbuilder.mapper;

import com.conal.dishbuilder.domain.DishEntity;
import com.conal.dishbuilder.domain.IngredientsEntity;
import com.conal.dishbuilder.dto.CreateDishRequest;
import com.conal.dishbuilder.dto.UpdateDishRequest;
import com.conal.dishbuilder.dto.response.DishResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DishMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "ingredients", ignore = true)
    DishEntity toEntity(CreateDishRequest request);
    
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.username")
    @Mapping(target = "ingredients", expression = "java(mapIngredientsToInfo(entity.getIngredients()))")
    DishResponse toResponse(DishEntity entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "ingredients", ignore = true)
    void updateEntity(UpdateDishRequest request, @MappingTarget DishEntity entity);
    
    default List<DishResponse.IngredientInfo> mapIngredientsToInfo(List<IngredientsEntity> ingredients) {
        if (ingredients == null) {
            return null;
        }
        return ingredients.stream()
                .map(ingredient -> DishResponse.IngredientInfo.builder()
                        .id(ingredient.getId())
                        .name(ingredient.getName())
                        .description(ingredient.getDescription())
                        .price(ingredient.getPrice())
                        .categoryId(ingredient.getCategory().getId())
                        .categoryName(ingredient.getCategory().getName())
                        .build())
                .toList();
    }
}
