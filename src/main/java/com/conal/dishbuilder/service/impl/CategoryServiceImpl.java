package com.conal.dishbuilder.service.impl;

import com.conal.dishbuilder.context.TenantContextHolder;
import com.conal.dishbuilder.domain.CategoryEntity;
import com.conal.dishbuilder.dto.CreateCategoryRequest;
import com.conal.dishbuilder.dto.UpdateCategoryRequest;
import com.conal.dishbuilder.dto.request.filter.CategoryFilterRequest;
import com.conal.dishbuilder.dto.response.CategoryResponse;
import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.exception.MultipleFieldValidationException;
import com.conal.dishbuilder.exception.NotFoundException;
import com.conal.dishbuilder.mapper.CategoryMapper;
import com.conal.dishbuilder.repository.CategoryRepository;
import com.conal.dishbuilder.repository.querydsl.CategoryQueryDslRepository;
import com.conal.dishbuilder.service.CategoryService;
import com.conal.dishbuilder.validator.CategoryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryValidator categoryValidator;
    private final CategoryMapper categoryMapper;
    private final CategoryQueryDslRepository categoryQueryDslRepository;

    @Override
    public CategoryResponse addCategory(CreateCategoryRequest category) {
        UUID tenantId = TenantContextHolder.getTenantContext();
        List<FieldErrorResponse> responses = categoryValidator.validateCreateCategory(category);
        if (!responses.isEmpty()) {
            throw new MultipleFieldValidationException(responses);
        }

        CategoryEntity entity = categoryMapper.toEntity(category);
        entity.setTenantId(tenantId);
        CategoryEntity savedEntity = categoryRepository.save(entity);
        return categoryMapper.toResponse(savedEntity);
    }

    @Override
    public CategoryResponse updateCategory(UUID id, UpdateCategoryRequest category) {
        CategoryEntity existingEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));

        // Validate tenant access
        if (!existingEntity.getTenantId().equals(TenantContextHolder.getTenantContext())) {
            throw new NotFoundException("Category not found with id: " + id);
        }

        List<FieldErrorResponse> responses = categoryValidator.validateUpdateCategory(category, existingEntity);
        if (!responses.isEmpty()) {
            throw new MultipleFieldValidationException(responses);
        }

        categoryMapper.updateEntity(category, existingEntity);
        CategoryEntity savedEntity = categoryRepository.save(existingEntity);
        return categoryMapper.toResponse(savedEntity);
    }

    @Override
    public CategoryResponse getCategoryById(UUID id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));

        // Validate tenant access
        if (!entity.getTenantId().equals(TenantContextHolder.getTenantContext())) {
            throw new NotFoundException("Category not found with id: " + id);
        }

        return categoryMapper.toResponse(entity);
    }

    @Override
    public PageResponse<CategoryResponse> getCategories(CategoryFilterRequest filterRequest) {
        // Set tenant filter
        filterRequest.setTenantId(TenantContextHolder.getTenantContext());

        // Create sort
        Sort sort = Sort.by(Sort.Direction.fromString(filterRequest.getSortDirection()), filterRequest.getSortBy());

        // Create pageable
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);

        return categoryQueryDslRepository.findAll(filterRequest, pageable);
    }

    @Override
    public boolean deleteCategory(UUID id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));

        // Validate tenant access
        if (!entity.getTenantId().equals(TenantContextHolder.getTenantContext())) {
            throw new NotFoundException("Category not found with id: " + id);
        }

        categoryRepository.delete(entity);
        return true;
    }
}
