package com.conal.dishbuilder.mapper;

import com.conal.dishbuilder.domain.DishEntity;
import com.conal.dishbuilder.domain.IngredientsEntity;
import com.conal.dishbuilder.domain.UserEntity;
import com.conal.dishbuilder.dto.CreateDishRequest;
import com.conal.dishbuilder.dto.UpdateDishRequest;
import com.conal.dishbuilder.dto.response.DishResponse;
import com.conal.dishbuilder.util.TestDataBuilder;
import com.conal.dishbuilder.util.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.config.import-check.enabled=false"
})
class DishMapperTest {

    @Autowired
    private DishMapper dishMapper;

    private UUID dishId;
    private UUID userId;
    private UUID ingredientId1;
    private UUID ingredientId2;
    private UUID tenantId;
    private UserEntity userEntity;
    private IngredientsEntity ingredient1;
    private IngredientsEntity ingredient2;
    private DishEntity dishEntity;
    private CreateDishRequest createRequest;
    private UpdateDishRequest updateRequest;

    @BeforeEach
    void setUp() {
        dishId = TestConstants.TEST_DISH_ID;
        userId = TestConstants.TEST_USER_ID;
        ingredientId1 = TestConstants.TEST_INGREDIENT_ID_1;
        ingredientId2 = TestConstants.TEST_INGREDIENT_ID_2;
        tenantId = TestConstants.TEST_TENANT_ID;
        
        userEntity = TestDataBuilder.UserBuilder.builder()
                .id(userId)
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .tenantId(tenantId)
                .buildEntity();
        
        ingredient1 = TestDataBuilder.IngredientsBuilder.builder()
                .id(ingredientId1)
                .name("Ingredient 1")
                .description("Description 1")
                .price(new BigDecimal("5.00"))
                .tenantId(tenantId)
                .buildEntity();
        
        ingredient2 = TestDataBuilder.IngredientsBuilder.builder()
                .id(ingredientId2)
                .name("Ingredient 2")
                .description("Description 2")
                .price(new BigDecimal("7.50"))
                .tenantId(tenantId)
                .buildEntity();
        
        dishEntity = TestDataBuilder.DishBuilder.builder()
                .id(dishId)
                .name("Test Dish")
                .description("Test Description")
                .totalPrice(new BigDecimal("15.00"))
                .discount(new BigDecimal("2.00"))
                .vat(new BigDecimal("1.30"))
                .tenantId(tenantId)
                .user(userEntity)
                .ingredients(Arrays.asList(ingredient1, ingredient2))
                .buildEntity();
        
        createRequest = TestDataBuilder.DishBuilder.builder()
                .name("New Dish")
                .description("New Description")
                .totalPrice(new BigDecimal("20.00"))
                .discount(new BigDecimal("3.00"))
                .vat(new BigDecimal("1.70"))
                .userId(userId)
                .ingredientIds(Arrays.asList(ingredientId1, ingredientId2))
                .buildCreateRequest();
        
        updateRequest = TestDataBuilder.DishBuilder.builder()
                .name("Updated Dish")
                .description("Updated Description")
                .totalPrice(new BigDecimal("25.00"))
                .discount(new BigDecimal("4.00"))
                .vat(new BigDecimal("2.10"))
                .userId(userId)
                .ingredientIds(Arrays.asList(ingredientId1))
                .buildUpdateRequest();
    }

    @Test
    void toEntity_FromCreateRequest_Success() {
        // When
        DishEntity entity = dishMapper.toEntity(createRequest);

        // Then
        assertNotNull(entity);
        assertNull(entity.getId()); // Should be null for new entity
        assertNull(entity.getTenantId()); // Should be null, will be set by service
        assertEquals("New Dish", entity.getName());
        assertEquals("New Description", entity.getDescription());
        assertEquals(new BigDecimal("20.00"), entity.getTotalPrice());
        assertEquals(new BigDecimal("3.00"), entity.getDiscount());
        assertEquals(new BigDecimal("1.70"), entity.getVat());
        assertNull(entity.getUser()); // Should be null, will be set by service
        assertNull(entity.getIngredients()); // Should be null, will be set by service
    }

    @Test
    void toEntity_FromCreateRequest_NullValues() {
        // Given
        CreateDishRequest nullRequest = new CreateDishRequest();
        nullRequest.setName(null);
        nullRequest.setDescription(null);
        nullRequest.setTotalPrice(null);
        nullRequest.setDiscount(null);
        nullRequest.setVat(null);
        nullRequest.setUserId(null);
        nullRequest.setIngredientIds(null);

        // When
        DishEntity entity = dishMapper.toEntity(nullRequest);

        // Then
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getTenantId());
        assertNull(entity.getName());
        assertNull(entity.getDescription());
        assertNull(entity.getTotalPrice());
        assertNull(entity.getDiscount());
        assertNull(entity.getVat());
        assertNull(entity.getUser());
        assertNull(entity.getIngredients());
    }

    @Test
    void toResponse_FromEntity_Success() {
        // Given
        dishEntity.setCreatedBy(tenantId);
        dishEntity.setCreatedAt(LocalDateTime.now());
        dishEntity.setUpdatedBy(tenantId);
        dishEntity.setUpdatedAt(LocalDateTime.now());

        // When
        DishResponse response = dishMapper.toResponse(dishEntity);

        // Then
        assertNotNull(response);
        assertEquals(dishId, response.getId());
        assertEquals("Test Dish", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals(new BigDecimal("15.00"), response.getTotalPrice());
        assertEquals(new BigDecimal("2.00"), response.getDiscount());
        assertEquals(new BigDecimal("1.30"), response.getVat());
        assertEquals(tenantId, response.getTenantId());
        assertEquals(userId, response.getUserId());
        assertEquals("testuser", response.getUserName());
        assertEquals(2, response.getIngredients().size());
        assertEquals(tenantId, response.getCreatedBy());
        assertNotNull(response.getCreatedAt());
        assertEquals(tenantId, response.getUpdatedBy());
        assertNotNull(response.getUpdatedAt());
    }

    @Test
    void toResponse_FromEntity_NullValues() {
        // Given
        DishEntity nullEntity = new DishEntity();
        nullEntity.setId(dishId);
        nullEntity.setName(null);
        nullEntity.setDescription(null);
        nullEntity.setTotalPrice(null);
        nullEntity.setDiscount(null);
        nullEntity.setVat(null);
        nullEntity.setTenantId(null);
        nullEntity.setUser(null);
        nullEntity.setIngredients(null);

        // When
        DishResponse response = dishMapper.toResponse(nullEntity);

        // Then
        assertNotNull(response);
        assertEquals(dishId, response.getId());
        assertNull(response.getName());
        assertNull(response.getDescription());
        assertNull(response.getTotalPrice());
        assertNull(response.getDiscount());
        assertNull(response.getVat());
        assertNull(response.getTenantId());
        assertNull(response.getUserId());
        assertNull(response.getUserName());
        assertNull(response.getIngredients());
        assertNull(response.getCreatedBy());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedBy());
        assertNull(response.getUpdatedAt());
    }

    @Test
    void updateEntity_FromUpdateRequest_Success() {
        // Given
        DishEntity existingEntity = TestDataBuilder.DishBuilder.builder()
                .id(dishId)
                .name("Original Name")
                .description("Original Description")
                .totalPrice(new BigDecimal("10.00"))
                .discount(new BigDecimal("1.00"))
                .vat(new BigDecimal("0.90"))
                .tenantId(tenantId)
                .user(userEntity)
                .ingredients(Arrays.asList(ingredient1, ingredient2))
                .buildEntity();

        // When
        dishMapper.updateEntity(updateRequest, existingEntity);

        // Then
        assertEquals(dishId, existingEntity.getId()); // Should not change
        assertEquals(tenantId, existingEntity.getTenantId()); // Should not change
        assertEquals("Updated Dish", existingEntity.getName());
        assertEquals("Updated Description", existingEntity.getDescription());
        assertEquals(new BigDecimal("25.00"), existingEntity.getTotalPrice());
        assertEquals(new BigDecimal("4.00"), existingEntity.getDiscount());
        assertEquals(new BigDecimal("2.10"), existingEntity.getVat());
        assertEquals(userEntity, existingEntity.getUser()); // Should not change
        assertEquals(Arrays.asList(ingredient1, ingredient2), existingEntity.getIngredients()); // Should not change
    }

    @Test
    void updateEntity_FromUpdateRequest_NullValues() {
        // Given
        DishEntity existingEntity = TestDataBuilder.DishBuilder.builder()
                .id(dishId)
                .name("Original Name")
                .description("Original Description")
                .totalPrice(new BigDecimal("10.00"))
                .discount(new BigDecimal("1.00"))
                .vat(new BigDecimal("0.90"))
                .tenantId(tenantId)
                .user(userEntity)
                .ingredients(Arrays.asList(ingredient1, ingredient2))
                .buildEntity();

        UpdateDishRequest nullRequest = new UpdateDishRequest();
        nullRequest.setName(null);
        nullRequest.setDescription(null);
        nullRequest.setTotalPrice(null);
        nullRequest.setDiscount(null);
        nullRequest.setVat(null);

        // When
        dishMapper.updateEntity(nullRequest, existingEntity);

        // Then
        assertEquals(dishId, existingEntity.getId()); // Should not change
        assertEquals(tenantId, existingEntity.getTenantId()); // Should not change
        assertEquals("Original Name", existingEntity.getName()); // Should not change
        assertEquals("Original Description", existingEntity.getDescription()); // Should not change
        assertEquals(new BigDecimal("10.00"), existingEntity.getTotalPrice()); // Should not change
        assertEquals(new BigDecimal("1.00"), existingEntity.getDiscount()); // Should not change
        assertEquals(new BigDecimal("0.90"), existingEntity.getVat()); // Should not change
        assertEquals(userEntity, existingEntity.getUser()); // Should not change
        assertEquals(Arrays.asList(ingredient1, ingredient2), existingEntity.getIngredients()); // Should not change
    }

    @Test
    void updateEntity_FromUpdateRequest_PartialUpdate() {
        // Given
        DishEntity existingEntity = TestDataBuilder.DishBuilder.builder()
                .id(dishId)
                .name("Original Name")
                .description("Original Description")
                .totalPrice(new BigDecimal("10.00"))
                .discount(new BigDecimal("1.00"))
                .vat(new BigDecimal("0.90"))
                .tenantId(tenantId)
                .user(userEntity)
                .ingredients(Arrays.asList(ingredient1, ingredient2))
                .buildEntity();

        UpdateDishRequest partialRequest = new UpdateDishRequest();
        partialRequest.setName("Updated Name");
        partialRequest.setTotalPrice(new BigDecimal("15.00"));
        // other fields are null

        // When
        dishMapper.updateEntity(partialRequest, existingEntity);

        // Then
        assertEquals(dishId, existingEntity.getId()); // Should not change
        assertEquals(tenantId, existingEntity.getTenantId()); // Should not change
        assertEquals("Updated Name", existingEntity.getName());
        assertEquals("Original Description", existingEntity.getDescription()); // Should not change
        assertEquals(new BigDecimal("15.00"), existingEntity.getTotalPrice());
        assertEquals(new BigDecimal("1.00"), existingEntity.getDiscount()); // Should not change
        assertEquals(new BigDecimal("0.90"), existingEntity.getVat()); // Should not change
        assertEquals(userEntity, existingEntity.getUser()); // Should not change
        assertEquals(Arrays.asList(ingredient1, ingredient2), existingEntity.getIngredients()); // Should not change
    }

    @Test
    void toResponse_FromEntity_WithAuditFields() {
        // Given
        UUID createdBy = UUID.randomUUID();
        UUID updatedBy = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        dishEntity.setCreatedBy(createdBy);
        dishEntity.setCreatedAt(createdAt);
        dishEntity.setUpdatedBy(updatedBy);
        dishEntity.setUpdatedAt(updatedAt);

        // When
        DishResponse response = dishMapper.toResponse(dishEntity);

        // Then
        assertNotNull(response);
        assertEquals(dishId, response.getId());
        assertEquals("Test Dish", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals(new BigDecimal("15.00"), response.getTotalPrice());
        assertEquals(new BigDecimal("2.00"), response.getDiscount());
        assertEquals(new BigDecimal("1.30"), response.getVat());
        assertEquals(tenantId, response.getTenantId());
        assertEquals(userId, response.getUserId());
        assertEquals("testuser", response.getUserName());
        assertEquals(2, response.getIngredients().size());
        assertEquals(createdBy, response.getCreatedBy());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedBy, response.getUpdatedBy());
        assertEquals(updatedAt, response.getUpdatedAt());
    }

    @Test
    void toEntity_FromCreateRequest_WithSpecialCharacters() {
        // Given
        CreateDishRequest specialRequest = new CreateDishRequest();
        specialRequest.setName("Dish with Special Characters: @#$%^&*()");
        specialRequest.setDescription("Description with Special Characters: @#$%^&*()");
        specialRequest.setTotalPrice(new BigDecimal("99.99"));
        specialRequest.setDiscount(new BigDecimal("9.99"));
        specialRequest.setVat(new BigDecimal("9.00"));
        specialRequest.setUserId(userId);
        specialRequest.setIngredientIds(Arrays.asList(ingredientId1, ingredientId2));

        // When
        DishEntity entity = dishMapper.toEntity(specialRequest);

        // Then
        assertNotNull(entity);
        assertEquals("Dish with Special Characters: @#$%^&*()", entity.getName());
        assertEquals("Description with Special Characters: @#$%^&*()", entity.getDescription());
        assertEquals(new BigDecimal("99.99"), entity.getTotalPrice());
        assertEquals(new BigDecimal("9.99"), entity.getDiscount());
        assertEquals(new BigDecimal("9.00"), entity.getVat());
    }

    @Test
    void updateEntity_FromUpdateRequest_WithSpecialCharacters() {
        // Given
        DishEntity existingEntity = TestDataBuilder.DishBuilder.builder()
                .id(dishId)
                .name("Original Name")
                .description("Original Description")
                .totalPrice(new BigDecimal("10.00"))
                .discount(new BigDecimal("1.00"))
                .vat(new BigDecimal("0.90"))
                .tenantId(tenantId)
                .user(userEntity)
                .ingredients(Arrays.asList(ingredient1, ingredient2))
                .buildEntity();

        UpdateDishRequest specialRequest = new UpdateDishRequest();
        specialRequest.setName("Updated with Special Characters: @#$%^&*()");
        specialRequest.setDescription("Updated Description with Special Characters: @#$%^&*()");
        specialRequest.setTotalPrice(new BigDecimal("99.99"));
        specialRequest.setDiscount(new BigDecimal("9.99"));
        specialRequest.setVat(new BigDecimal("9.00"));

        // When
        dishMapper.updateEntity(specialRequest, existingEntity);

        // Then
        assertEquals("Updated with Special Characters: @#$%^&*()", existingEntity.getName());
        assertEquals("Updated Description with Special Characters: @#$%^&*()", existingEntity.getDescription());
        assertEquals(new BigDecimal("99.99"), existingEntity.getTotalPrice());
        assertEquals(new BigDecimal("9.99"), existingEntity.getDiscount());
        assertEquals(new BigDecimal("9.00"), existingEntity.getVat());
    }

    @Test
    void toResponse_FromEntity_WithNullUser() {
        // Given
        dishEntity.setUser(null);

        // When
        DishResponse response = dishMapper.toResponse(dishEntity);

        // Then
        assertNotNull(response);
        assertEquals(dishId, response.getId());
        assertEquals("Test Dish", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals(new BigDecimal("15.00"), response.getTotalPrice());
        assertEquals(new BigDecimal("2.00"), response.getDiscount());
        assertEquals(new BigDecimal("1.30"), response.getVat());
        assertEquals(tenantId, response.getTenantId());
        assertNull(response.getUserId());
        assertNull(response.getUserName());
        assertEquals(2, response.getIngredients().size());
    }

    @Test
    void toResponse_FromEntity_WithNullIngredients() {
        // Given
        dishEntity.setIngredients(null);

        // When
        DishResponse response = dishMapper.toResponse(dishEntity);

        // Then
        assertNotNull(response);
        assertEquals(dishId, response.getId());
        assertEquals("Test Dish", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals(new BigDecimal("15.00"), response.getTotalPrice());
        assertEquals(new BigDecimal("2.00"), response.getDiscount());
        assertEquals(new BigDecimal("1.30"), response.getVat());
        assertEquals(tenantId, response.getTenantId());
        assertEquals(userId, response.getUserId());
        assertEquals("testuser", response.getUserName());
        assertNull(response.getIngredients());
    }

    @Test
    void toEntity_FromCreateRequest_WithZeroValues() {
        // Given
        CreateDishRequest zeroRequest = new CreateDishRequest();
        zeroRequest.setName("Free Dish");
        zeroRequest.setDescription("Free Description");
        zeroRequest.setTotalPrice(BigDecimal.ZERO);
        zeroRequest.setDiscount(BigDecimal.ZERO);
        zeroRequest.setVat(BigDecimal.ZERO);
        zeroRequest.setUserId(userId);
        zeroRequest.setIngredientIds(Arrays.asList(ingredientId1));

        // When
        DishEntity entity = dishMapper.toEntity(zeroRequest);

        // Then
        assertNotNull(entity);
        assertEquals("Free Dish", entity.getName());
        assertEquals("Free Description", entity.getDescription());
        assertEquals(BigDecimal.ZERO, entity.getTotalPrice());
        assertEquals(BigDecimal.ZERO, entity.getDiscount());
        assertEquals(BigDecimal.ZERO, entity.getVat());
    }

    @Test
    void updateEntity_FromUpdateRequest_WithZeroValues() {
        // Given
        DishEntity existingEntity = TestDataBuilder.DishBuilder.builder()
                .id(dishId)
                .name("Original Name")
                .description("Original Description")
                .totalPrice(new BigDecimal("10.00"))
                .discount(new BigDecimal("1.00"))
                .vat(new BigDecimal("0.90"))
                .tenantId(tenantId)
                .user(userEntity)
                .ingredients(Arrays.asList(ingredient1, ingredient2))
                .buildEntity();

        UpdateDishRequest zeroRequest = new UpdateDishRequest();
        zeroRequest.setTotalPrice(BigDecimal.ZERO);
        zeroRequest.setDiscount(BigDecimal.ZERO);
        zeroRequest.setVat(BigDecimal.ZERO);

        // When
        dishMapper.updateEntity(zeroRequest, existingEntity);

        // Then
        assertEquals(BigDecimal.ZERO, existingEntity.getTotalPrice());
        assertEquals(BigDecimal.ZERO, existingEntity.getDiscount());
        assertEquals(BigDecimal.ZERO, existingEntity.getVat());
    }
}
