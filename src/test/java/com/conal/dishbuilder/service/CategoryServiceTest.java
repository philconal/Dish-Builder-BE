package com.conal.dishbuilder.service;

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
import com.conal.dishbuilder.service.impl.CategoryServiceImpl;
import com.conal.dishbuilder.validator.CategoryValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryValidator categoryValidator;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CategoryQueryDslRepository categoryQueryDslRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private UUID tenantId;
    private UUID categoryId;
    private CategoryEntity categoryEntity;
    private CategoryResponse categoryResponse;
    private CreateCategoryRequest createRequest;
    private UpdateCategoryRequest updateRequest;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        categoryEntity = CategoryEntity.builder()
                .id(categoryId)
                .name("Test Category")
                .description("Test Description")
                .tenantId(tenantId)
                .build();

        categoryResponse = CategoryResponse.builder()
                .id(categoryId)
                .name("Test Category")
                .description("Test Description")
                .tenantId(tenantId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = new CreateCategoryRequest();
        createRequest.setName("Test Category");
        createRequest.setDescription("Test Description");

        updateRequest = new UpdateCategoryRequest();
        updateRequest.setName("Updated Category");
        updateRequest.setDescription("Updated Description");
    }

    @Test
    void addCategory_Success() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(categoryValidator.validateCreateCategory(createRequest)).thenReturn(Collections.emptyList());
            when(categoryMapper.toEntity(createRequest)).thenReturn(categoryEntity);
            when(categoryRepository.save(any(CategoryEntity.class))).thenReturn(categoryEntity);
            when(categoryMapper.toResponse(categoryEntity)).thenReturn(categoryResponse);

            // When
            CategoryResponse result = categoryService.addCategory(createRequest);

            // Then
            assertNotNull(result);
            assertEquals(categoryResponse.getId(), result.getId());
            assertEquals(categoryResponse.getName(), result.getName());
            assertEquals(categoryResponse.getDescription(), result.getDescription());
            assertEquals(tenantId, result.getTenantId());

            verify(categoryValidator).validateCreateCategory(createRequest);
            verify(categoryMapper).toEntity(createRequest);
            verify(categoryRepository).save(any(CategoryEntity.class));
            verify(categoryMapper).toResponse(categoryEntity);
        }
    }

    @Test
    void addCategory_ValidationError() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            List<FieldErrorResponse> validationErrors = Arrays.asList(
                    FieldErrorResponse.builder()
                            .setField("name")
                            .setRejectedValue("Test")
                            .setMessage("Name already exists")
                            .build()
            );
            when(categoryValidator.validateCreateCategory(createRequest)).thenReturn(validationErrors);

            // When & Then
            MultipleFieldValidationException exception = assertThrows(
                    MultipleFieldValidationException.class,
                    () -> categoryService.addCategory(createRequest)
            );

            assertEquals(validationErrors, exception.getFieldErrors());
            verify(categoryValidator).validateCreateCategory(createRequest);
            verify(categoryRepository, never()).save(any(CategoryEntity.class));
        }
    }

    @Test
    void updateCategory_Success() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
            when(categoryValidator.validateUpdateCategory(updateRequest, categoryEntity)).thenReturn(Collections.emptyList());
            when(categoryRepository.save(categoryEntity)).thenReturn(categoryEntity);
            when(categoryMapper.toResponse(categoryEntity)).thenReturn(categoryResponse);

            // When
            CategoryResponse result = categoryService.updateCategory(categoryId, updateRequest);

            // Then
            assertNotNull(result);
            assertEquals(categoryResponse.getId(), result.getId());

            verify(categoryRepository).findById(categoryId);
            verify(categoryValidator).validateUpdateCategory(updateRequest, categoryEntity);
            verify(categoryRepository).save(categoryEntity);
            verify(categoryMapper).toResponse(categoryEntity);
        }
    }

    @Test
    void updateCategory_NotFound() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            // When & Then
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> categoryService.updateCategory(categoryId, updateRequest)
            );

            assertEquals("Category not found with id: " + categoryId, exception.getMessage());
            verify(categoryRepository).findById(categoryId);
            verify(categoryValidator, never()).validateUpdateCategory(any(), any());
        }
    }

    @Test
    void updateCategory_WrongTenant() {
        // Given
        UUID differentTenantId = UUID.randomUUID();
        categoryEntity.setTenantId(differentTenantId);
        
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));

            // When & Then
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> categoryService.updateCategory(categoryId, updateRequest)
            );

            assertEquals("Category not found with id: " + categoryId, exception.getMessage());
            verify(categoryRepository).findById(categoryId);
            verify(categoryValidator, never()).validateUpdateCategory(any(), any());
        }
    }

    @Test
    void getCategoryById_Success() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
            when(categoryMapper.toResponse(categoryEntity)).thenReturn(categoryResponse);

            // When
            CategoryResponse result = categoryService.getCategoryById(categoryId);

            // Then
            assertNotNull(result);
            assertEquals(categoryResponse.getId(), result.getId());
            assertEquals(categoryResponse.getName(), result.getName());

            verify(categoryRepository).findById(categoryId);
            verify(categoryMapper).toResponse(categoryEntity);
        }
    }

    @Test
    void getCategoryById_NotFound() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            // When & Then
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> categoryService.getCategoryById(categoryId)
            );

            assertEquals("Category not found with id: " + categoryId, exception.getMessage());
            verify(categoryRepository).findById(categoryId);
            verify(categoryMapper, never()).toResponse(any());
        }
    }

    @Test
    void getCategories_Success() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            CategoryFilterRequest filterRequest = new CategoryFilterRequest();
            PageResponse<CategoryResponse> pageResponse = PageResponse.<CategoryResponse>builder()
                    .data(Arrays.asList(categoryResponse))
                    .totalElements(1L)
                    .totalPages(1)
                    .size(10)
                    .page(0)
                    .build();

            when(categoryQueryDslRepository.findAll(any(CategoryFilterRequest.class), any(Pageable.class)))
                    .thenReturn(pageResponse);

            // When
            PageResponse<CategoryResponse> result = categoryService.getCategories(filterRequest);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getData().size());
            assertEquals(1L, result.getTotalElements());
            assertEquals(tenantId, filterRequest.getTenantId());

            verify(categoryQueryDslRepository).findAll(any(CategoryFilterRequest.class), any(Pageable.class));
        }
    }

    @Test
    void deleteCategory_Success() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));

            // When
            boolean result = categoryService.deleteCategory(categoryId);

            // Then
            assertTrue(result);
            verify(categoryRepository).findById(categoryId);
            verify(categoryRepository).delete(categoryEntity);
        }
    }

    @Test
    void deleteCategory_NotFound() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            // When & Then
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> categoryService.deleteCategory(categoryId)
            );

            assertEquals("Category not found with id: " + categoryId, exception.getMessage());
            verify(categoryRepository).findById(categoryId);
            verify(categoryRepository, never()).delete(any());
        }
    }
}
