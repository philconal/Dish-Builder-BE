package com.conal.dishbuilder.service.impl;

import com.conal.dishbuilder.context.TenantContextHolder;
import com.conal.dishbuilder.domain.DishEntity;
import com.conal.dishbuilder.domain.IngredientsEntity;
import com.conal.dishbuilder.domain.UserEntity;
import com.conal.dishbuilder.dto.CreateDishRequest;
import com.conal.dishbuilder.dto.UpdateDishRequest;
import com.conal.dishbuilder.dto.request.filter.DishFilterRequest;
import com.conal.dishbuilder.dto.response.DishResponse;
import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.exception.MultipleFieldValidationException;
import com.conal.dishbuilder.exception.NotFoundException;
import com.conal.dishbuilder.mapper.DishMapper;
import com.conal.dishbuilder.repository.DishRepository;
import com.conal.dishbuilder.repository.IngredientsRepository;
import com.conal.dishbuilder.repository.UserRepository;
import com.conal.dishbuilder.repository.querydsl.DishQueryDslRepository;
import com.conal.dishbuilder.service.DishService;
import com.conal.dishbuilder.validator.DishValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DishServiceImpl implements DishService {
    private final DishRepository dishRepository;
    private final UserRepository userRepository;
    private final IngredientsRepository ingredientsRepository;
    private final DishValidator dishValidator;
    private final DishMapper dishMapper;
    private final DishQueryDslRepository dishQueryDslRepository;

    @Override
    public DishResponse addDish(CreateDishRequest dish) {
        List<FieldErrorResponse> responses = dishValidator.validateCreateDish(dish);
        if (!responses.isEmpty()) {
            throw new MultipleFieldValidationException(responses);
        }

        DishEntity entity = dishMapper.toEntity(dish);
        entity.setTenantId(TenantContextHolder.getTenantContext());
        
        // Set user
        UserEntity user = userRepository.findById(dish.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + dish.getUserId()));
        entity.setUser(user);
        
        // Set ingredients
        List<IngredientsEntity> ingredients = ingredientsRepository.findAllById(dish.getIngredientIds());
        if (ingredients.size() != dish.getIngredientIds().size()) {
            throw new NotFoundException("One or more ingredients not found");
        }
        entity.setIngredients(ingredients);
        
        DishEntity savedEntity = dishRepository.save(entity);
        return dishMapper.toResponse(savedEntity);
    }

    @Override
    public DishResponse updateDish(UUID id, UpdateDishRequest dish) {
        DishEntity existingEntity = dishRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dish not found with id: " + id));

        // Validate tenant access
        if (!existingEntity.getTenantId().equals(TenantContextHolder.getTenantContext())) {
            throw new NotFoundException("Dish not found with id: " + id);
        }

        List<FieldErrorResponse> responses = dishValidator.validateUpdateDish(dish, existingEntity);
        if (!responses.isEmpty()) {
            throw new MultipleFieldValidationException(responses);
        }

        // Update user if provided
        if (dish.getUserId() != null) {
            UserEntity user = userRepository.findById(dish.getUserId())
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + dish.getUserId()));
            existingEntity.setUser(user);
        }

        // Update ingredients if provided
        if (dish.getIngredientIds() != null && !dish.getIngredientIds().isEmpty()) {
            List<IngredientsEntity> ingredients = ingredientsRepository.findAllById(dish.getIngredientIds());
            if (ingredients.size() != dish.getIngredientIds().size()) {
                throw new NotFoundException("One or more ingredients not found");
            }
            existingEntity.setIngredients(ingredients);
        }

        dishMapper.updateEntity(dish, existingEntity);
        DishEntity savedEntity = dishRepository.save(existingEntity);
        return dishMapper.toResponse(savedEntity);
    }

    @Override
    public DishResponse getDishById(UUID id) {
        DishEntity entity = dishRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dish not found with id: " + id));

        // Validate tenant access
        if (!entity.getTenantId().equals(TenantContextHolder.getTenantContext())) {
            throw new NotFoundException("Dish not found with id: " + id);
        }

        return dishMapper.toResponse(entity);
    }

    @Override
    public PageResponse<DishResponse> getDishes(DishFilterRequest filterRequest) {
        // Set tenant filter
        filterRequest.setTenantId(TenantContextHolder.getTenantContext());
        
        // Create sort
        Sort sort = Sort.by(Sort.Direction.fromString(filterRequest.getSortDirection()), filterRequest.getSortBy());
        
        // Create pageable
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);
        
        return dishQueryDslRepository.findAll(filterRequest, pageable);
    }

    @Override
    public boolean deleteDish(UUID id) {
        DishEntity entity = dishRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Dish not found with id: " + id));

        // Validate tenant access
        if (!entity.getTenantId().equals(TenantContextHolder.getTenantContext())) {
            throw new NotFoundException("Dish not found with id: " + id);
        }

        dishRepository.delete(entity);
        return true;
    }
}
