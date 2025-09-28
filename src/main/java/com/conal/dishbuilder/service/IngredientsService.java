package com.conal.dishbuilder.service;

import com.conal.dishbuilder.dto.CreateIngredientsRequest;
import com.conal.dishbuilder.dto.UpdateIngredientsRequest;
import com.conal.dishbuilder.dto.request.filter.IngredientsFilterRequest;
import com.conal.dishbuilder.dto.response.IngredientsResponse;
import com.conal.dishbuilder.dto.response.PageResponse;

import java.util.UUID;

public interface IngredientsService {
    IngredientsResponse addIngredients(CreateIngredientsRequest ingredients);
    IngredientsResponse updateIngredients(UUID id, UpdateIngredientsRequest ingredients);
    IngredientsResponse getIngredientsById(UUID id);
    PageResponse<IngredientsResponse> getIngredients(IngredientsFilterRequest filterRequest);
    boolean deleteIngredients(UUID id);
}
