package com.conal.dishbuilder.service;

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
import com.conal.dishbuilder.service.impl.IngredientsServiceImpl;
import com.conal.dishbuilder.validator.IngredientsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
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
class IngredientsServiceTest {

    @Mock
    private IngredientsRepository ingredientsRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private IngredientsValidator ingredientsValidator;

    @Mock
    private IngredientsMapper ingredientsMapper;

    @Mock
    private IngredientsQueryDslRepository ingredientsQueryDslRepository;

    @InjectMocks
    private IngredientsServiceImpl ingredientsService;

    private UUID tenantId;
    private UUID ingredientId;
    private UUID categoryId;
    private CategoryEntity categoryEntity;
    private IngredientsEntity ingredientsEntity;
    private IngredientsResponse ingredientsResponse;
    private CreateIngredientsRequest createRequest;
    private UpdateIngredientsRequest updateRequest;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        ingredientId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        categoryEntity = CategoryEntity.builder()
                .id(categoryId)
                .name("Test Category")
                .description("Test Category Description")
                .tenantId(tenantId)
                .build();

        ingredientsEntity = IngredientsEntity.builder()
                .id(ingredientId)
                .name("Test Ingredient")
                .description("Test Ingredient Description")
                .price(new BigDecimal("10.50"))
                .tenantId(tenantId)
                .category(categoryEntity)
                .build();

        ingredientsResponse = IngredientsResponse.builder()
                .id(ingredientId)
                .name("Test Ingredient")
                .description("Test Ingredient Description")
                .price(new BigDecimal("10.50"))
                .tenantId(tenantId)
                .categoryId(categoryId)
                .categoryName("Test Category")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = new CreateIngredientsRequest();
        createRequest.setName("Test Ingredient");
        createRequest.setDescription("Test Ingredient Description");
        createRequest.setPrice(new BigDecimal("10.50"));
        createRequest.setCategoryId(categoryId);

        updateRequest = new UpdateIngredientsRequest();
        updateRequest.setName("Updated Ingredient");
        updateRequest.setDescription("Updated Ingredient Description");
        updateRequest.setPrice(new BigDecimal("12.00"));
        updateRequest.setCategoryId(categoryId);
    }

    @Test
    void addIngredients_Success() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(ingredientsValidator.validateCreateIngredients(createRequest)).thenReturn(Collections.emptyList());
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
            when(ingredientsMapper.toEntity(createRequest)).thenReturn(ingredientsEntity);
            when(ingredientsRepository.save(any(IngredientsEntity.class))).thenReturn(ingredientsEntity);
            when(ingredientsMapper.toResponse(ingredientsEntity)).thenReturn(ingredientsResponse);

            // When
            IngredientsResponse result = ingredientsService.addIngredients(createRequest);

            // Then
            assertNotNull(result);
            assertEquals(ingredientsResponse.getId(), result.getId());
            assertEquals(ingredientsResponse.getName(), result.getName());
            assertEquals(ingredientsResponse.getDescription(), result.getDescription());
            assertEquals(ingredientsResponse.getPrice(), result.getPrice());
            assertEquals(tenantId, result.getTenantId());

            verify(ingredientsValidator).validateCreateIngredients(createRequest);
            verify(categoryRepository).findById(categoryId);
            verify(ingredientsMapper).toEntity(createRequest);
            verify(ingredientsRepository).save(any(IngredientsEntity.class));
            verify(ingredientsMapper).toResponse(ingredientsEntity);
        }
    }

    @Test
    void addIngredients_ValidationError() {
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
            when(ingredientsValidator.validateCreateIngredients(createRequest)).thenReturn(validationErrors);

            // When & Then
            MultipleFieldValidationException exception = assertThrows(
                    MultipleFieldValidationException.class,
                    () -> ingredientsService.addIngredients(createRequest)
            );

            assertEquals(validationErrors, exception.getFieldErrors());
            verify(ingredientsValidator).validateCreateIngredients(createRequest);
            verify(ingredientsRepository, never()).save(any(IngredientsEntity.class));
        }
    }

    @Test
    void addIngredients_CategoryNotFound() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(ingredientsValidator.validateCreateIngredients(createRequest)).thenReturn(Collections.emptyList());
            when(ingredientsMapper.toEntity(createRequest)).thenReturn(ingredientsEntity);
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            // When & Then
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> ingredientsService.addIngredients(createRequest)
            );

            assertEquals("Category not found with id: " + categoryId, exception.getMessage());
            verify(ingredientsValidator).validateCreateIngredients(createRequest);
            verify(categoryRepository).findById(categoryId);
            verify(ingredientsRepository, never()).save(any(IngredientsEntity.class));
        }
    }

    @Test
    void updateIngredients_Success() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(ingredientsRepository.findById(ingredientId)).thenReturn(Optional.of(ingredientsEntity));
            when(ingredientsValidator.validateUpdateIngredients(updateRequest, ingredientsEntity)).thenReturn(Collections.emptyList());
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(categoryEntity));
            when(ingredientsRepository.save(ingredientsEntity)).thenReturn(ingredientsEntity);
            when(ingredientsMapper.toResponse(ingredientsEntity)).thenReturn(ingredientsResponse);

            // When
            IngredientsResponse result = ingredientsService.updateIngredients(ingredientId, updateRequest);

            // Then
            assertNotNull(result);
            assertEquals(ingredientsResponse.getId(), result.getId());

            verify(ingredientsRepository).findById(ingredientId);
            verify(ingredientsValidator).validateUpdateIngredients(updateRequest, ingredientsEntity);
            verify(categoryRepository).findById(categoryId);
            verify(ingredientsRepository).save(ingredientsEntity);
            verify(ingredientsMapper).toResponse(ingredientsEntity);
        }
    }

    @Test
    void updateIngredients_NotFound() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(ingredientsRepository.findById(ingredientId)).thenReturn(Optional.empty());

            // When & Then
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> ingredientsService.updateIngredients(ingredientId, updateRequest)
            );

            assertEquals("Ingredients not found with id: " + ingredientId, exception.getMessage());
            verify(ingredientsRepository).findById(ingredientId);
            verify(ingredientsValidator, never()).validateUpdateIngredients(any(), any());
        }
    }

    @Test
    void updateIngredients_WrongTenant() {
        // Given
        UUID differentTenantId = UUID.randomUUID();
        ingredientsEntity.setTenantId(differentTenantId);
        
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(ingredientsRepository.findById(ingredientId)).thenReturn(Optional.of(ingredientsEntity));

            // When & Then
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> ingredientsService.updateIngredients(ingredientId, updateRequest)
            );

            assertEquals("Ingredients not found with id: " + ingredientId, exception.getMessage());
            verify(ingredientsRepository).findById(ingredientId);
            verify(ingredientsValidator, never()).validateUpdateIngredients(any(), any());
        }
    }

    @Test
    void getIngredientsById_Success() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(ingredientsRepository.findById(ingredientId)).thenReturn(Optional.of(ingredientsEntity));
            when(ingredientsMapper.toResponse(ingredientsEntity)).thenReturn(ingredientsResponse);

            // When
            IngredientsResponse result = ingredientsService.getIngredientsById(ingredientId);

            // Then
            assertNotNull(result);
            assertEquals(ingredientsResponse.getId(), result.getId());
            assertEquals(ingredientsResponse.getName(), result.getName());

            verify(ingredientsRepository).findById(ingredientId);
            verify(ingredientsMapper).toResponse(ingredientsEntity);
        }
    }

    @Test
    void getIngredientsById_NotFound() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(ingredientsRepository.findById(ingredientId)).thenReturn(Optional.empty());

            // When & Then
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> ingredientsService.getIngredientsById(ingredientId)
            );

            assertEquals("Ingredients not found with id: " + ingredientId, exception.getMessage());
            verify(ingredientsRepository).findById(ingredientId);
            verify(ingredientsMapper, never()).toResponse(any());
        }
    }

    @Test
    void getIngredients_Success() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            IngredientsFilterRequest filterRequest = new IngredientsFilterRequest();
            PageResponse<IngredientsResponse> pageResponse = PageResponse.<IngredientsResponse>builder()
                    .data(Arrays.asList(ingredientsResponse))
                    .totalElements(1L)
                    .totalPages(1)
                    .size(10)
                    .page(0)
                    .build();

            when(ingredientsQueryDslRepository.findAll(any(IngredientsFilterRequest.class), any(Pageable.class)))
                    .thenReturn(pageResponse);

            // When
            PageResponse<IngredientsResponse> result = ingredientsService.getIngredients(filterRequest);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getData().size());
            assertEquals(1L, result.getTotalElements());
            assertEquals(tenantId, filterRequest.getTenantId());

            verify(ingredientsQueryDslRepository).findAll(any(IngredientsFilterRequest.class), any(Pageable.class));
        }
    }

    @Test
    void deleteIngredients_Success() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(ingredientsRepository.findById(ingredientId)).thenReturn(Optional.of(ingredientsEntity));

            // When
            boolean result = ingredientsService.deleteIngredients(ingredientId);

            // Then
            assertTrue(result);
            verify(ingredientsRepository).findById(ingredientId);
            verify(ingredientsRepository).delete(ingredientsEntity);
        }
    }

    @Test
    void deleteIngredients_NotFound() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(ingredientsRepository.findById(ingredientId)).thenReturn(Optional.empty());

            // When & Then
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> ingredientsService.deleteIngredients(ingredientId)
            );

            assertEquals("Ingredients not found with id: " + ingredientId, exception.getMessage());
            verify(ingredientsRepository).findById(ingredientId);
            verify(ingredientsRepository, never()).delete(any());
        }
    }
}
