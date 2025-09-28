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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TestDataBuilder {

    public static class CategoryBuilder {
        private UUID id = UUID.randomUUID();
        private String name = "Test Category";
        private String description = "Test Category Description";
        private UUID tenantId = UUID.randomUUID();
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();

        public static CategoryBuilder builder() {
            return new CategoryBuilder();
        }

        public CategoryBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public CategoryBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CategoryBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CategoryBuilder tenantId(UUID tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public CategoryEntity buildEntity() {
        return CategoryEntity.builder()
                .id(id)
                .name(name)
                .description(description)
                .tenantId(tenantId)
                .build();
        }

        public CategoryResponse buildResponse() {
            return CategoryResponse.builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .tenantId(tenantId)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();
        }

        public CreateCategoryRequest buildCreateRequest() {
            CreateCategoryRequest request = new CreateCategoryRequest();
            request.setName(name);
            request.setDescription(description);
            return request;
        }

        public UpdateCategoryRequest buildUpdateRequest() {
            UpdateCategoryRequest request = new UpdateCategoryRequest();
            request.setName(name);
            request.setDescription(description);
            return request;
        }
    }

    public static class IngredientsBuilder {
        private UUID id = UUID.randomUUID();
        private String name = "Test Ingredient";
        private String description = "Test Ingredient Description";
        private BigDecimal price = new BigDecimal("10.50");
        private UUID tenantId = UUID.randomUUID();
        private CategoryEntity category;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();

        public static IngredientsBuilder builder() {
            return new IngredientsBuilder();
        }

        public IngredientsBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public IngredientsBuilder name(String name) {
            this.name = name;
            return this;
        }

        public IngredientsBuilder description(String description) {
            this.description = description;
            return this;
        }

        public IngredientsBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public IngredientsBuilder tenantId(UUID tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public IngredientsBuilder category(CategoryEntity category) {
            this.category = category;
            return this;
        }

        public IngredientsBuilder categoryId(UUID categoryId) {
            // Create a mock category entity for testing
            this.category = CategoryEntity.builder()
                    .id(categoryId)
                    .name("Test Category")
                    .description("Test Category Description")
                    .build();
            return this;
        }

        public IngredientsEntity buildEntity() {
        return IngredientsEntity.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .tenantId(tenantId)
                .category(category)
                .build();
        }

        public IngredientsResponse buildResponse() {
            return IngredientsResponse.builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .price(price)
                    .tenantId(tenantId)
                    .categoryId(category != null ? category.getId() : null)
                    .categoryName(category != null ? category.getName() : null)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();
        }

        public CreateIngredientsRequest buildCreateRequest() {
            CreateIngredientsRequest request = new CreateIngredientsRequest();
            request.setName(name);
            request.setDescription(description);
            request.setPrice(price);
            request.setCategoryId(category != null ? category.getId() : null);
            return request;
        }

        public UpdateIngredientsRequest buildUpdateRequest() {
            UpdateIngredientsRequest request = new UpdateIngredientsRequest();
            request.setName(name);
            request.setDescription(description);
            request.setPrice(price);
            request.setCategoryId(category != null ? category.getId() : null);
            return request;
        }
    }

    public static class UserBuilder {
        private UUID id = UUID.randomUUID();
        private String username = "testuser";
        private String firstName = "Test";
        private String lastName = "User";
        private String email = "test@example.com";
        private String phone = "1234567890";
        private UUID tenantId = UUID.randomUUID();

        public static UserBuilder builder() {
            return new UserBuilder();
        }

        public UserBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserBuilder tenantId(UUID tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public UserEntity buildEntity() {
            return UserEntity.builder()
                    .id(id)
                    .username(username)
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .phone(phone)
                    .tenantId(tenantId)
                    .build();
        }
    }

    public static class DishBuilder {
        private UUID id = UUID.randomUUID();
        private String name = "Test Dish";
        private String description = "Test Dish Description";
        private BigDecimal totalPrice = new BigDecimal("15.00");
        private BigDecimal discount = new BigDecimal("2.00");
        private BigDecimal vat = new BigDecimal("1.30");
        private UUID tenantId = UUID.randomUUID();
        private UserEntity user;
        private List<IngredientsEntity> ingredients;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();

        public static DishBuilder builder() {
            return new DishBuilder();
        }

        public DishBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public DishBuilder name(String name) {
            this.name = name;
            return this;
        }

        public DishBuilder description(String description) {
            this.description = description;
            return this;
        }

        public DishBuilder totalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public DishBuilder discount(BigDecimal discount) {
            this.discount = discount;
            return this;
        }

        public DishBuilder vat(BigDecimal vat) {
            this.vat = vat;
            return this;
        }

        public DishBuilder tenantId(UUID tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public DishBuilder user(UserEntity user) {
            this.user = user;
            return this;
        }

        public DishBuilder userId(UUID userId) {
            // Create a mock user entity for testing
            this.user = UserEntity.builder()
                    .id(userId)
                    .username("testuser")
                    .firstName("Test")
                    .lastName("User")
                    .build();
            return this;
        }

        public DishBuilder ingredients(List<IngredientsEntity> ingredients) {
            this.ingredients = ingredients;
            return this;
        }

        public DishBuilder ingredientIds(List<UUID> ingredientIds) {
            // Create mock ingredient entities for testing
            this.ingredients = ingredientIds.stream()
                    .map(id -> IngredientsEntity.builder()
                            .id(id)
                            .name("Test Ingredient")
                            .description("Test Ingredient Description")
                            .price(new BigDecimal("10.00"))
                            .build())
                    .toList();
            return this;
        }

        public DishEntity buildEntity() {
        return DishEntity.builder()
                .id(id)
                .name(name)
                .description(description)
                .totalPrice(totalPrice)
                .discount(discount)
                .vat(vat)
                .tenantId(tenantId)
                .user(user)
                .ingredients(ingredients != null ? ingredients : Arrays.asList())
                .build();
        }

        public DishResponse buildResponse() {
            return DishResponse.builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .totalPrice(totalPrice)
                    .discount(discount)
                    .vat(vat)
                    .tenantId(tenantId)
                    .userId(user != null ? user.getId() : null)
                    .userName(user != null ? user.getUsername() : null)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();
        }

        public CreateDishRequest buildCreateRequest() {
            CreateDishRequest request = new CreateDishRequest();
            request.setName(name);
            request.setDescription(description);
            request.setTotalPrice(totalPrice);
            request.setDiscount(discount);
            request.setVat(vat);
            request.setUserId(user != null ? user.getId() : null);
            request.setIngredientIds(ingredients != null ? 
                    ingredients.stream().map(IngredientsEntity::getId).toList() : Arrays.asList());
            return request;
        }

        public UpdateDishRequest buildUpdateRequest() {
            UpdateDishRequest request = new UpdateDishRequest();
            request.setName(name);
            request.setDescription(description);
            request.setTotalPrice(totalPrice);
            request.setDiscount(discount);
            request.setVat(vat);
            request.setUserId(user != null ? user.getId() : null);
            request.setIngredientIds(ingredients != null ? 
                    ingredients.stream().map(IngredientsEntity::getId).toList() : Arrays.asList());
            return request;
        }
    }
}
