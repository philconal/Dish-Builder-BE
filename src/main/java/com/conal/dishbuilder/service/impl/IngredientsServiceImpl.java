package com.conal.dishbuilder.service.impl;

import com.conal.dishbuilder.context.TenantContextHolder;
import com.conal.dishbuilder.domain.CategoryEntity;
import com.conal.dishbuilder.domain.IngredientsEntity;
import com.conal.dishbuilder.dto.CreateIngredientsRequest;
import com.conal.dishbuilder.dto.UpdateIngredientsRequest;
import com.conal.dishbuilder.dto.request.filter.IngredientsFilterRequest;
import com.conal.dishbuilder.dto.response.IngredientsResponse;
import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.exception.MultipleFieldValidationException;
import com.conal.dishbuilder.exception.NotFoundException;
import com.conal.dishbuilder.mapper.IngredientsMapper;
import com.conal.dishbuilder.repository.CategoryRepository;
import com.conal.dishbuilder.repository.IngredientsRepository;
import com.conal.dishbuilder.repository.querydsl.IngredientsQueryDslRepository;
import com.conal.dishbuilder.service.IngredientsService;
import com.conal.dishbuilder.validator.IngredientsValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IngredientsServiceImpl implements IngredientsService {
    private final IngredientsRepository ingredientsRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientsValidator ingredientsValidator;
    private final IngredientsMapper ingredientsMapper;
    private final IngredientsQueryDslRepository ingredientsQueryDslRepository;

    @Override
    public IngredientsResponse addIngredients(CreateIngredientsRequest ingredients) {
        List<FieldErrorResponse> responses = ingredientsValidator.validateCreateIngredients(ingredients);
        if (!responses.isEmpty()) {
            throw new MultipleFieldValidationException(responses);
        }

        IngredientsEntity entity = ingredientsMapper.toEntity(ingredients);
        entity.setTenantId(TenantContextHolder.getTenantContext());
        
        // Set category
        CategoryEntity category = categoryRepository.findById(ingredients.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + ingredients.getCategoryId()));
        entity.setCategory(category);
        
        IngredientsEntity savedEntity = ingredientsRepository.save(entity);
        return ingredientsMapper.toResponse(savedEntity);
    }

    @Override
    public IngredientsResponse updateIngredients(UUID id, UpdateIngredientsRequest ingredients) {
        IngredientsEntity existingEntity = ingredientsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ingredients not found with id: " + id));

        // Validate tenant access
        if (!existingEntity.getTenantId().equals(TenantContextHolder.getTenantContext())) {
            throw new NotFoundException("Ingredients not found with id: " + id);
        }

        List<FieldErrorResponse> responses = ingredientsValidator.validateUpdateIngredients(ingredients, existingEntity);
        if (!responses.isEmpty()) {
            throw new MultipleFieldValidationException(responses);
        }

        // Update category if provided
        if (ingredients.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(ingredients.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + ingredients.getCategoryId()));
            existingEntity.setCategory(category);
        }

        ingredientsMapper.updateEntity(ingredients, existingEntity);
        IngredientsEntity savedEntity = ingredientsRepository.save(existingEntity);
        return ingredientsMapper.toResponse(savedEntity);
    }

    @Override
    public IngredientsResponse getIngredientsById(UUID id) {
        IngredientsEntity entity = ingredientsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ingredients not found with id: " + id));

        // Validate tenant access
        if (!entity.getTenantId().equals(TenantContextHolder.getTenantContext())) {
            throw new NotFoundException("Ingredients not found with id: " + id);
        }

        return ingredientsMapper.toResponse(entity);
    }

    @Override
    public PageResponse<IngredientsResponse> getIngredients(IngredientsFilterRequest filterRequest) {
        // Set tenant filter
        filterRequest.setTenantId(TenantContextHolder.getTenantContext());
        
        // Create sort
        Sort sort = Sort.by(Sort.Direction.fromString(filterRequest.getSortDirection()), filterRequest.getSortBy());
        
        // Create pageable
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);
        
        return ingredientsQueryDslRepository.findAll(filterRequest, pageable);
    }

    @Override
    public boolean deleteIngredients(UUID id) {
        IngredientsEntity entity = ingredientsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ingredients not found with id: " + id));

        // Validate tenant access
        if (!entity.getTenantId().equals(TenantContextHolder.getTenantContext())) {
            throw new NotFoundException("Ingredients not found with id: " + id);
        }

        ingredientsRepository.delete(entity);
        return true;
    }
}
