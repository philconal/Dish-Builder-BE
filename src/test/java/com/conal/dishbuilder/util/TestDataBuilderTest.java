package com.conal.dishbuilder.util;

import com.conal.dishbuilder.domain.CategoryEntity;
import com.conal.dishbuilder.domain.DishEntity;
import com.conal.dishbuilder.domain.IngredientsEntity;
import com.conal.dishbuilder.domain.UserEntity;
import com.conal.dishbuilder.dto.CreateCategoryRequest;
import com.conal.dishbuilder.dto.CreateDishRequest;
import com.conal.dishbuilder.dto.CreateIngredientsRequest;
import com.conal.dishbuilder.dto.UpdateCategoryRequest;
import com.conal.dishbuilder.dto.UpdateDishRequest;
import com.conal.dishbuilder.dto.UpdateIngredientsRequest;
import com.conal.dishbuilder.dto.response.CategoryResponse;
import com.conal.dishbuilder.dto.response.DishResponse;
import com.conal.dishbuilder.dto.response.IngredientsResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TestDataBuilderTest {

    @Test
    void categoryBuilder_BuildEntity_Success() {
        // Given
        UUID categoryId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        // When
        CategoryEntity entity = TestDataBuilder.CategoryBuilder.builder()
                .id(categoryId)
                .name("Test Category")
                .description("Test Description")
                .tenantId(tenantId)
                .buildEntity();

        // Then
        assertNotNull(entity);
        assertEquals(categoryId, entity.getId());
        assertEquals("Test Category", entity.getName());
        assertEquals("Test Description", entity.getDescription());
        assertEquals(tenantId, entity.getTenantId());
    }

    @Test
    void categoryBuilder_BuildCreateRequest_Success() {
        // When
        CreateCategoryRequest request = TestDataBuilder.CategoryBuilder.builder()
                .name("New Category")
                .description("New Description")
                .buildCreateRequest();

        // Then
        assertNotNull(request);
        assertEquals("New Category", request.getName());
        assertEquals("New Description", request.getDescription());
    }

    @Test
    void categoryBuilder_BuildUpdateRequest_Success() {
        // When
        UpdateCategoryRequest request = TestDataBuilder.CategoryBuilder.builder()
                .name("Updated Category")
                .description("Updated Description")
                .buildUpdateRequest();

        // Then
        assertNotNull(request);
        assertEquals("Updated Category", request.getName());
        assertEquals("Updated Description", request.getDescription());
    }

    @Test
    void categoryBuilder_BuildResponse_Success() {
        // Given
        UUID categoryId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        // When
        CategoryResponse response = TestDataBuilder.CategoryBuilder.builder()
                .id(categoryId)
                .name("Test Category")
                .description("Test Description")
                .tenantId(tenantId)
                .buildResponse();

        // Then
        assertNotNull(response);
        assertEquals(categoryId, response.getId());
        assertEquals("Test Category", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals(tenantId, response.getTenantId());
    }

    @Test
    void ingredientsBuilder_BuildEntity_Success() {
        // Given
        UUID ingredientId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        CategoryEntity category = new CategoryEntity();
        category.setId(categoryId);

        // When
        IngredientsEntity entity = TestDataBuilder.IngredientsBuilder.builder()
                .id(ingredientId)
                .name("Test Ingredient")
                .description("Test Description")
                .price(new BigDecimal("10.50"))
                .tenantId(tenantId)
                .category(category)
                .buildEntity();

        // Then
        assertNotNull(entity);
        assertEquals(ingredientId, entity.getId());
        assertEquals("Test Ingredient", entity.getName());
        assertEquals("Test Description", entity.getDescription());
        assertEquals(new BigDecimal("10.50"), entity.getPrice());
        assertEquals(tenantId, entity.getTenantId());
        assertEquals(category, entity.getCategory());
    }

    @Test
    void ingredientsBuilder_BuildCreateRequest_Success() {
        // Given
        UUID categoryId = UUID.randomUUID();

        // When
        CreateIngredientsRequest request = TestDataBuilder.IngredientsBuilder.builder()
                .name("New Ingredient")
                .description("New Description")
                .price(new BigDecimal("15.00"))
                .categoryId(categoryId)
                .buildCreateRequest();

        // Then
        assertNotNull(request);
        assertEquals("New Ingredient", request.getName());
        assertEquals("New Description", request.getDescription());
        assertEquals(new BigDecimal("15.00"), request.getPrice());
        assertEquals(categoryId, request.getCategoryId());
    }

    @Test
    void ingredientsBuilder_BuildUpdateRequest_Success() {
        // Given
        UUID categoryId = UUID.randomUUID();

        // When
        UpdateIngredientsRequest request = TestDataBuilder.IngredientsBuilder.builder()
                .name("Updated Ingredient")
                .description("Updated Description")
                .price(new BigDecimal("20.00"))
                .categoryId(categoryId)
                .buildUpdateRequest();

        // Then
        assertNotNull(request);
        assertEquals("Updated Ingredient", request.getName());
        assertEquals("Updated Description", request.getDescription());
        assertEquals(new BigDecimal("20.00"), request.getPrice());
        assertEquals(categoryId, request.getCategoryId());
    }

    @Test
    void ingredientsBuilder_BuildResponse_Success() {
        // Given
        UUID ingredientId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        // When
        IngredientsResponse response = TestDataBuilder.IngredientsBuilder.builder()
                .id(ingredientId)
                .name("Test Ingredient")
                .description("Test Description")
                .price(new BigDecimal("10.50"))
                .tenantId(tenantId)
                .categoryId(categoryId)
                .buildResponse();

        // Then
        assertNotNull(response);
        assertEquals(ingredientId, response.getId());
        assertEquals("Test Ingredient", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals(new BigDecimal("10.50"), response.getPrice());
        assertEquals(tenantId, response.getTenantId());
        assertEquals(categoryId, response.getCategoryId());
    }

    @Test
    void dishBuilder_BuildEntity_Success() {
        // Given
        UUID dishId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setId(userId);
        List<IngredientsEntity> ingredients = Arrays.asList(new IngredientsEntity(), new IngredientsEntity());

        // When
        DishEntity entity = TestDataBuilder.DishBuilder.builder()
                .id(dishId)
                .name("Test Dish")
                .description("Test Description")
                .totalPrice(new BigDecimal("15.00"))
                .discount(new BigDecimal("2.00"))
                .vat(new BigDecimal("1.30"))
                .tenantId(tenantId)
                .user(user)
                .ingredients(ingredients)
                .buildEntity();

        // Then
        assertNotNull(entity);
        assertEquals(dishId, entity.getId());
        assertEquals("Test Dish", entity.getName());
        assertEquals("Test Description", entity.getDescription());
        assertEquals(new BigDecimal("15.00"), entity.getTotalPrice());
        assertEquals(new BigDecimal("2.00"), entity.getDiscount());
        assertEquals(new BigDecimal("1.30"), entity.getVat());
        assertEquals(tenantId, entity.getTenantId());
        assertEquals(user, entity.getUser());
        assertEquals(ingredients, entity.getIngredients());
    }

    @Test
    void dishBuilder_BuildCreateRequest_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        List<UUID> ingredientIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());

        // When
        CreateDishRequest request = TestDataBuilder.DishBuilder.builder()
                .name("New Dish")
                .description("New Description")
                .totalPrice(new BigDecimal("20.00"))
                .discount(new BigDecimal("3.00"))
                .vat(new BigDecimal("1.70"))
                .userId(userId)
                .ingredientIds(ingredientIds)
                .buildCreateRequest();

        // Then
        assertNotNull(request);
        assertEquals("New Dish", request.getName());
        assertEquals("New Description", request.getDescription());
        assertEquals(new BigDecimal("20.00"), request.getTotalPrice());
        assertEquals(new BigDecimal("3.00"), request.getDiscount());
        assertEquals(new BigDecimal("1.70"), request.getVat());
        assertEquals(userId, request.getUserId());
        assertEquals(ingredientIds, request.getIngredientIds());
    }

    @Test
    void dishBuilder_BuildUpdateRequest_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        List<UUID> ingredientIds = Arrays.asList(UUID.randomUUID());

        // When
        UpdateDishRequest request = TestDataBuilder.DishBuilder.builder()
                .name("Updated Dish")
                .description("Updated Description")
                .totalPrice(new BigDecimal("25.00"))
                .discount(new BigDecimal("4.00"))
                .vat(new BigDecimal("2.10"))
                .userId(userId)
                .ingredientIds(ingredientIds)
                .buildUpdateRequest();

        // Then
        assertNotNull(request);
        assertEquals("Updated Dish", request.getName());
        assertEquals("Updated Description", request.getDescription());
        assertEquals(new BigDecimal("25.00"), request.getTotalPrice());
        assertEquals(new BigDecimal("4.00"), request.getDiscount());
        assertEquals(new BigDecimal("2.10"), request.getVat());
        assertEquals(userId, request.getUserId());
        assertEquals(ingredientIds, request.getIngredientIds());
    }

    @Test
    void dishBuilder_BuildResponse_Success() {
        // Given
        UUID dishId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        // When
        DishResponse response = TestDataBuilder.DishBuilder.builder()
                .id(dishId)
                .name("Test Dish")
                .description("Test Description")
                .totalPrice(new BigDecimal("15.00"))
                .discount(new BigDecimal("2.00"))
                .vat(new BigDecimal("1.30"))
                .tenantId(tenantId)
                .userId(userId)
                .buildResponse();

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
    }

    @Test
    void userBuilder_BuildEntity_Success() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        // When
        UserEntity entity = TestDataBuilder.UserBuilder.builder()
                .id(userId)
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .tenantId(tenantId)
                .buildEntity();

        // Then
        assertNotNull(entity);
        assertEquals(userId, entity.getId());
        assertEquals("testuser", entity.getUsername());
        assertEquals("Test", entity.getFirstName());
        assertEquals("User", entity.getLastName());
        assertEquals(tenantId, entity.getTenantId());
    }

    @Test
    void testConstants_Values_Success() {
        // When & Then
        assertNotNull(TestConstants.TEST_TENANT_ID);
        assertNotNull(TestConstants.TEST_CATEGORY_ID);
        assertNotNull(TestConstants.TEST_INGREDIENT_ID_1);
        assertNotNull(TestConstants.TEST_INGREDIENT_ID_2);
        assertNotNull(TestConstants.TEST_DISH_ID);
        assertNotNull(TestConstants.TEST_USER_ID);
    }

    @Test
    void testConstants_UniqueValues_Success() {
        // When & Then
        assertNotEquals(TestConstants.TEST_TENANT_ID, TestConstants.TEST_CATEGORY_ID);
        assertNotEquals(TestConstants.TEST_CATEGORY_ID, TestConstants.TEST_INGREDIENT_ID_1);
        assertNotEquals(TestConstants.TEST_INGREDIENT_ID_1, TestConstants.TEST_INGREDIENT_ID_2);
        assertNotEquals(TestConstants.TEST_INGREDIENT_ID_2, TestConstants.TEST_DISH_ID);
        assertNotEquals(TestConstants.TEST_DISH_ID, TestConstants.TEST_USER_ID);
    }

    @Test
    void builder_WithNullValues_Success() {
        // When
        CategoryEntity entity = TestDataBuilder.CategoryBuilder.builder()
                .id(null)
                .name(null)
                .description(null)
                .tenantId(null)
                .buildEntity();

        // Then
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getName());
        assertNull(entity.getDescription());
        assertNull(entity.getTenantId());
    }

    @Test
    void builder_WithEmptyStrings_Success() {
        // When
        CategoryEntity entity = TestDataBuilder.CategoryBuilder.builder()
                .name("")
                .description("")
                .buildEntity();

        // Then
        assertNotNull(entity);
        assertEquals("", entity.getName());
        assertEquals("", entity.getDescription());
    }

    @Test
    void builder_WithSpecialCharacters_Success() {
        // When
        CategoryEntity entity = TestDataBuilder.CategoryBuilder.builder()
                .name("Category with Special Characters: @#$%^&*()")
                .description("Description with Special Characters: @#$%^&*()")
                .buildEntity();

        // Then
        assertNotNull(entity);
        assertEquals("Category with Special Characters: @#$%^&*()", entity.getName());
        assertEquals("Description with Special Characters: @#$%^&*()", entity.getDescription());
    }

    @Test
    void builder_WithZeroValues_Success() {
        // When
        IngredientsEntity entity = TestDataBuilder.IngredientsBuilder.builder()
                .price(BigDecimal.ZERO)
                .buildEntity();

        // Then
        assertNotNull(entity);
        assertEquals(BigDecimal.ZERO, entity.getPrice());
    }

    @Test
    void builder_WithNegativeValues_Success() {
        // When
        IngredientsEntity entity = TestDataBuilder.IngredientsBuilder.builder()
                .price(new BigDecimal("-1.00"))
                .buildEntity();

        // Then
        assertNotNull(entity);
        assertEquals(new BigDecimal("-1.00"), entity.getPrice());
    }
}
