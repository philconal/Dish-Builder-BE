package com.conal.dishbuilder.service;

import com.conal.dishbuilder.context.TenantContextHolder;
import com.conal.dishbuilder.domain.CategoryEntity;
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
import com.conal.dishbuilder.service.impl.DishServiceImpl;
import com.conal.dishbuilder.validator.DishValidator;
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
class DishServiceTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private IngredientsRepository ingredientsRepository;

    @Mock
    private DishValidator dishValidator;

    @Mock
    private DishMapper dishMapper;

    @Mock
    private DishQueryDslRepository dishQueryDslRepository;

    @InjectMocks
    private DishServiceImpl dishService;

    private UUID tenantId;
    private UUID dishId;
    private UUID userId;
    private UUID ingredientId1;
    private UUID ingredientId2;
    private UUID categoryId;
    
    private UserEntity userEntity;
    private CategoryEntity categoryEntity;
    private IngredientsEntity ingredientEntity1;
    private IngredientsEntity ingredientEntity2;
    private DishEntity dishEntity;
    private DishResponse dishResponse;
    private CreateDishRequest createRequest;
    private UpdateDishRequest updateRequest;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        dishId = UUID.randomUUID();
        userId = UUID.randomUUID();
        ingredientId1 = UUID.randomUUID();
        ingredientId2 = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        userEntity = UserEntity.builder()
                .id(userId)
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .tenantId(tenantId)
                .build();

        categoryEntity = CategoryEntity.builder()
                .id(categoryId)
                .name("Test Category")
                .description("Test Category Description")
                .tenantId(tenantId)
                .build();

        ingredientEntity1 = IngredientsEntity.builder()
                .id(ingredientId1)
                .name("Ingredient 1")
                .description("Test Ingredient 1")
                .price(new BigDecimal("5.00"))
                .tenantId(tenantId)
                .category(categoryEntity)
                .build();

        ingredientEntity2 = IngredientsEntity.builder()
                .id(ingredientId2)
                .name("Ingredient 2")
                .description("Test Ingredient 2")
                .price(new BigDecimal("7.50"))
                .tenantId(tenantId)
                .category(categoryEntity)
                .build();

        dishEntity = DishEntity.builder()
                .id(dishId)
                .name("Test Dish")
                .description("Test Dish Description")
                .totalPrice(new BigDecimal("15.00"))
                .discount(new BigDecimal("2.00"))
                .vat(new BigDecimal("1.30"))
                .tenantId(tenantId)
                .user(userEntity)
                .ingredients(Arrays.asList(ingredientEntity1, ingredientEntity2))
                .build();

        dishResponse = DishResponse.builder()
                .id(dishId)
                .name("Test Dish")
                .description("Test Dish Description")
                .totalPrice(new BigDecimal("15.00"))
                .discount(new BigDecimal("2.00"))
                .vat(new BigDecimal("1.30"))
                .tenantId(tenantId)
                .userId(userId)
                .userName("testuser")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = new CreateDishRequest();
        createRequest.setName("Test Dish");
        createRequest.setDescription("Test Dish Description");
        createRequest.setTotalPrice(new BigDecimal("15.00"));
        createRequest.setDiscount(new BigDecimal("2.00"));
        createRequest.setVat(new BigDecimal("1.30"));
        createRequest.setUserId(userId);
        createRequest.setIngredientIds(Arrays.asList(ingredientId1, ingredientId2));

        updateRequest = new UpdateDishRequest();
        updateRequest.setName("Updated Dish");
        updateRequest.setDescription("Updated Dish Description");
        updateRequest.setTotalPrice(new BigDecimal("18.00"));
        updateRequest.setDiscount(new BigDecimal("3.00"));
        updateRequest.setVat(new BigDecimal("1.50"));
        updateRequest.setUserId(userId);
        updateRequest.setIngredientIds(Arrays.asList(ingredientId1));
    }

    @Test
    void addDish_Success() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(dishValidator.validateCreateDish(createRequest)).thenReturn(Collections.emptyList());
            when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
            when(ingredientsRepository.findAllById(createRequest.getIngredientIds()))
                    .thenReturn(Arrays.asList(ingredientEntity1, ingredientEntity2));
            when(dishMapper.toEntity(createRequest)).thenReturn(dishEntity);
            when(dishRepository.save(any(DishEntity.class))).thenReturn(dishEntity);
            when(dishMapper.toResponse(dishEntity)).thenReturn(dishResponse);

            // When
            DishResponse result = dishService.addDish(createRequest);

            // Then
            assertNotNull(result);
            assertEquals(dishResponse.getId(), result.getId());
            assertEquals(dishResponse.getName(), result.getName());
            assertEquals(dishResponse.getDescription(), result.getDescription());
            assertEquals(dishResponse.getTotalPrice(), result.getTotalPrice());
            assertEquals(tenantId, result.getTenantId());

            verify(dishValidator).validateCreateDish(createRequest);
            verify(userRepository).findById(userId);
            verify(ingredientsRepository).findAllById(createRequest.getIngredientIds());
            verify(dishMapper).toEntity(createRequest);
            verify(dishRepository).save(any(DishEntity.class));
            verify(dishMapper).toResponse(dishEntity);
        }
    }

    @Test
    void addDish_ValidationError() {
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
            when(dishValidator.validateCreateDish(createRequest)).thenReturn(validationErrors);

            // When & Then
            MultipleFieldValidationException exception = assertThrows(
                    MultipleFieldValidationException.class,
                    () -> dishService.addDish(createRequest)
            );

            assertEquals(validationErrors, exception.getFieldErrors());
            verify(dishValidator).validateCreateDish(createRequest);
            verify(dishRepository, never()).save(any(DishEntity.class));
        }
    }

    @Test
    void addDish_UserNotFound() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(dishValidator.validateCreateDish(createRequest)).thenReturn(Collections.emptyList());
            when(dishMapper.toEntity(createRequest)).thenReturn(dishEntity);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // When & Then
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> dishService.addDish(createRequest)
            );

            assertEquals("User not found with id: " + userId, exception.getMessage());
            verify(dishValidator).validateCreateDish(createRequest);
            verify(userRepository).findById(userId);
            verify(dishRepository, never()).save(any(DishEntity.class));
        }
    }

    @Test
    void addDish_IngredientsNotFound() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(dishValidator.validateCreateDish(createRequest)).thenReturn(Collections.emptyList());
            when(dishMapper.toEntity(createRequest)).thenReturn(dishEntity);
            when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
            when(ingredientsRepository.findAllById(createRequest.getIngredientIds()))
                    .thenReturn(Arrays.asList(ingredientEntity1)); // Only one ingredient found

            // When & Then
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> dishService.addDish(createRequest)
            );

            assertEquals("One or more ingredients not found", exception.getMessage());
            verify(dishValidator).validateCreateDish(createRequest);
            verify(userRepository).findById(userId);
            verify(ingredientsRepository).findAllById(createRequest.getIngredientIds());
            verify(dishRepository, never()).save(any(DishEntity.class));
        }
    }

    @Test
    void updateDish_Success() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(dishRepository.findById(dishId)).thenReturn(Optional.of(dishEntity));
            when(dishValidator.validateUpdateDish(updateRequest, dishEntity)).thenReturn(Collections.emptyList());
            when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
            when(ingredientsRepository.findAllById(updateRequest.getIngredientIds()))
                    .thenReturn(Arrays.asList(ingredientEntity1));
            when(dishRepository.save(dishEntity)).thenReturn(dishEntity);
            when(dishMapper.toResponse(dishEntity)).thenReturn(dishResponse);

            // When
            DishResponse result = dishService.updateDish(dishId, updateRequest);

            // Then
            assertNotNull(result);
            assertEquals(dishResponse.getId(), result.getId());

            verify(dishRepository).findById(dishId);
            verify(dishValidator).validateUpdateDish(updateRequest, dishEntity);
            verify(userRepository).findById(userId);
            verify(ingredientsRepository).findAllById(updateRequest.getIngredientIds());
            verify(dishRepository).save(dishEntity);
            verify(dishMapper).toResponse(dishEntity);
        }
    }

    @Test
    void updateDish_NotFound() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(dishRepository.findById(dishId)).thenReturn(Optional.empty());

            // When & Then
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> dishService.updateDish(dishId, updateRequest)
            );

            assertEquals("Dish not found with id: " + dishId, exception.getMessage());
            verify(dishRepository).findById(dishId);
            verify(dishValidator, never()).validateUpdateDish(any(), any());
        }
    }

    @Test
    void updateDish_WrongTenant() {
        // Given
        UUID differentTenantId = UUID.randomUUID();
        dishEntity.setTenantId(differentTenantId);
        
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(dishRepository.findById(dishId)).thenReturn(Optional.of(dishEntity));

            // When & Then
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> dishService.updateDish(dishId, updateRequest)
            );

            assertEquals("Dish not found with id: " + dishId, exception.getMessage());
            verify(dishRepository).findById(dishId);
            verify(dishValidator, never()).validateUpdateDish(any(), any());
        }
    }

    @Test
    void getDishById_Success() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(dishRepository.findById(dishId)).thenReturn(Optional.of(dishEntity));
            when(dishMapper.toResponse(dishEntity)).thenReturn(dishResponse);

            // When
            DishResponse result = dishService.getDishById(dishId);

            // Then
            assertNotNull(result);
            assertEquals(dishResponse.getId(), result.getId());
            assertEquals(dishResponse.getName(), result.getName());

            verify(dishRepository).findById(dishId);
            verify(dishMapper).toResponse(dishEntity);
        }
    }

    @Test
    void getDishById_NotFound() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(dishRepository.findById(dishId)).thenReturn(Optional.empty());

            // When & Then
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> dishService.getDishById(dishId)
            );

            assertEquals("Dish not found with id: " + dishId, exception.getMessage());
            verify(dishRepository).findById(dishId);
            verify(dishMapper, never()).toResponse(any());
        }
    }

    @Test
    void getDishes_Success() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            DishFilterRequest filterRequest = new DishFilterRequest();
            PageResponse<DishResponse> pageResponse = PageResponse.<DishResponse>builder()
                    .data(Arrays.asList(dishResponse))
                    .totalElements(1L)
                    .totalPages(1)
                    .size(10)
                    .page(0)
                    .build();

            when(dishQueryDslRepository.findAll(any(DishFilterRequest.class), any(Pageable.class)))
                    .thenReturn(pageResponse);

            // When
            PageResponse<DishResponse> result = dishService.getDishes(filterRequest);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getData().size());
            assertEquals(1L, result.getTotalElements());
            assertEquals(tenantId, filterRequest.getTenantId());

            verify(dishQueryDslRepository).findAll(any(DishFilterRequest.class), any(Pageable.class));
        }
    }

    @Test
    void deleteDish_Success() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(dishRepository.findById(dishId)).thenReturn(Optional.of(dishEntity));

            // When
            boolean result = dishService.deleteDish(dishId);

            // Then
            assertTrue(result);
            verify(dishRepository).findById(dishId);
            verify(dishRepository).delete(dishEntity);
        }
    }

    @Test
    void deleteDish_NotFound() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(dishRepository.findById(dishId)).thenReturn(Optional.empty());

            // When & Then
            NotFoundException exception = assertThrows(
                    NotFoundException.class,
                    () -> dishService.deleteDish(dishId)
            );

            assertEquals("Dish not found with id: " + dishId, exception.getMessage());
            verify(dishRepository).findById(dishId);
            verify(dishRepository, never()).delete(any());
        }
    }
}
