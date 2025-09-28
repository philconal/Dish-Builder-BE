package com.conal.dishbuilder.validator;

import com.conal.dishbuilder.domain.IngredientsEntity;
import com.conal.dishbuilder.dto.CreateIngredientsRequest;
import com.conal.dishbuilder.dto.UpdateIngredientsRequest;
import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IngredientsValidator {
    List<FieldErrorResponse> validateCreateIngredients(CreateIngredientsRequest request);
    List<FieldErrorResponse> validateUpdateIngredients(UpdateIngredientsRequest request, IngredientsEntity ingredients);
}
