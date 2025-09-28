package com.conal.dishbuilder.validator;

import com.conal.dishbuilder.domain.DishEntity;
import com.conal.dishbuilder.dto.CreateDishRequest;
import com.conal.dishbuilder.dto.UpdateDishRequest;
import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DishValidator {
    List<FieldErrorResponse> validateCreateDish(CreateDishRequest request);
    List<FieldErrorResponse> validateUpdateDish(UpdateDishRequest request, DishEntity dish);
}
