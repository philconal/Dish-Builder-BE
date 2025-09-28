package com.conal.dishbuilder.service;

import com.conal.dishbuilder.dto.CreateDishRequest;
import com.conal.dishbuilder.dto.UpdateDishRequest;
import com.conal.dishbuilder.dto.request.filter.DishFilterRequest;
import com.conal.dishbuilder.dto.response.DishResponse;
import com.conal.dishbuilder.dto.response.PageResponse;

import java.util.UUID;

public interface DishService {
    DishResponse addDish(CreateDishRequest dish);
    DishResponse updateDish(UUID id, UpdateDishRequest dish);
    DishResponse getDishById(UUID id);
    PageResponse<DishResponse> getDishes(DishFilterRequest filterRequest);
    boolean deleteDish(UUID id);
}
