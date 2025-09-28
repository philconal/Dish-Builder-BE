package com.conal.dishbuilder.mapper;

import com.conal.dishbuilder.domain.CategoryEntity;
import com.conal.dishbuilder.domain.IngredientsEntity;
import com.conal.dishbuilder.dto.CreateIngredientsRequest;
import com.conal.dishbuilder.dto.UpdateIngredientsRequest;
import com.conal.dishbuilder.dto.response.IngredientsResponse;
import com.conal.dishbuilder.util.TestDataBuilder;
import com.conal.dishbuilder.util.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.config.import-check.enabled=false"
})
class IngredientsMapperTest {

    @Autowired
    private IngredientsMapper ingredientsMapper;

    private UUID ingredientId;
    private UUID categoryId;
    private UUID tenantId;
    private CategoryEntity categoryEntity;
    private IngredientsEntity ingredientsEntity;
    private CreateIngredientsRequest createRequest;
    private UpdateIngredientsRequest updateRequest;

    @BeforeEach
    void setUp() {
        ingredientId = TestConstants.TEST_INGREDIENT_ID_1;
        categoryId = TestConstants.TEST_CATEGORY_ID;
        tenantId = TestConstants.TEST_TENANT_ID;
        
        categoryEntity = TestDataBuilder.CategoryBuilder.builder()
                .id(categoryId)
                .name("Test Category")
                .description("Test Category Description")
                .tenantId(tenantId)
                .buildEntity();
        
        ingredientsEntity = TestDataBuilder.IngredientsBuilder.builder()
                .id(ingredientId)
                .name("Test Ingredient")
                .description("Test Description")
                .price(new BigDecimal("10.50"))
                .tenantId(tenantId)
                .category(categoryEntity)
                .buildEntity();
        
        createRequest = TestDataBuilder.IngredientsBuilder.builder()
                .name("New Ingredient")
                .description("New Description")
                .price(new BigDecimal("15.00"))
                .categoryId(categoryId)
                .buildCreateRequest();
        
        updateRequest = TestDataBuilder.IngredientsBuilder.builder()
                .name("Updated Ingredient")
                .description("Updated Description")
                .price(new BigDecimal("20.00"))
                .categoryId(categoryId)
                .buildUpdateRequest();
    }

    @Test
    void toEntity_FromCreateRequest_Success() {
        // When
        IngredientsEntity entity = ingredientsMapper.toEntity(createRequest);

        // Then
        assertNotNull(entity);
        assertNull(entity.getId()); // Should be null for new entity
        assertNull(entity.getTenantId()); // Should be null, will be set by service
        assertEquals("New Ingredient", entity.getName());
        assertEquals("New Description", entity.getDescription());
        assertEquals(new BigDecimal("15.00"), entity.getPrice());
        assertNull(entity.getCategory()); // Should be null, will be set by service
    }

    @Test
    void toEntity_FromCreateRequest_NullValues() {
        // Given
        CreateIngredientsRequest nullRequest = new CreateIngredientsRequest();
        nullRequest.setName(null);
        nullRequest.setDescription(null);
        nullRequest.setPrice(null);
        nullRequest.setCategoryId(null);

        // When
        IngredientsEntity entity = ingredientsMapper.toEntity(nullRequest);

        // Then
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getTenantId());
        assertNull(entity.getName());
        assertNull(entity.getDescription());
        assertNull(entity.getPrice());
        assertNull(entity.getCategory());
    }

    @Test
    void toResponse_FromEntity_Success() {
        // Given
        ingredientsEntity.setCreatedBy(tenantId);
        ingredientsEntity.setCreatedAt(LocalDateTime.now());
        ingredientsEntity.setUpdatedBy(tenantId);
        ingredientsEntity.setUpdatedAt(LocalDateTime.now());

        // When
        IngredientsResponse response = ingredientsMapper.toResponse(ingredientsEntity);

        // Then
        assertNotNull(response);
        assertEquals(ingredientId, response.getId());
        assertEquals("Test Ingredient", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals(new BigDecimal("10.50"), response.getPrice());
        assertEquals(tenantId, response.getTenantId());
        assertEquals(categoryId, response.getCategoryId());
        assertEquals("Test Category", response.getCategoryName());
        assertEquals(tenantId, response.getCreatedBy());
        assertNotNull(response.getCreatedAt());
        assertEquals(tenantId, response.getUpdatedBy());
        assertNotNull(response.getUpdatedAt());
    }

    @Test
    void toResponse_FromEntity_NullValues() {
        // Given
        IngredientsEntity nullEntity = new IngredientsEntity();
        nullEntity.setId(ingredientId);
        nullEntity.setName(null);
        nullEntity.setDescription(null);
        nullEntity.setPrice(null);
        nullEntity.setTenantId(null);
        nullEntity.setCategory(null);

        // When
        IngredientsResponse response = ingredientsMapper.toResponse(nullEntity);

        // Then
        assertNotNull(response);
        assertEquals(ingredientId, response.getId());
        assertNull(response.getName());
        assertNull(response.getDescription());
        assertNull(response.getPrice());
        assertNull(response.getTenantId());
        assertNull(response.getCategoryId());
        assertNull(response.getCategoryName());
        assertNull(response.getCreatedBy());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedBy());
        assertNull(response.getUpdatedAt());
    }

    @Test
    void updateEntity_FromUpdateRequest_Success() {
        // Given
        IngredientsEntity existingEntity = TestDataBuilder.IngredientsBuilder.builder()
                .id(ingredientId)
                .name("Original Name")
                .description("Original Description")
                .price(new BigDecimal("5.00"))
                .tenantId(tenantId)
                .category(categoryEntity)
                .buildEntity();

        // When
        ingredientsMapper.updateEntity(updateRequest, existingEntity);

        // Then
        assertEquals(ingredientId, existingEntity.getId()); // Should not change
        assertEquals(tenantId, existingEntity.getTenantId()); // Should not change
        assertEquals("Updated Ingredient", existingEntity.getName());
        assertEquals("Updated Description", existingEntity.getDescription());
        assertEquals(new BigDecimal("20.00"), existingEntity.getPrice());
        assertEquals(categoryEntity, existingEntity.getCategory()); // Should not change
    }

    @Test
    void updateEntity_FromUpdateRequest_NullValues() {
        // Given
        IngredientsEntity existingEntity = TestDataBuilder.IngredientsBuilder.builder()
                .id(ingredientId)
                .name("Original Name")
                .description("Original Description")
                .price(new BigDecimal("5.00"))
                .tenantId(tenantId)
                .category(categoryEntity)
                .buildEntity();

        UpdateIngredientsRequest nullRequest = new UpdateIngredientsRequest();
        nullRequest.setName(null);
        nullRequest.setDescription(null);
        nullRequest.setPrice(null);

        // When
        ingredientsMapper.updateEntity(nullRequest, existingEntity);

        // Then
        assertEquals(ingredientId, existingEntity.getId()); // Should not change
        assertEquals(tenantId, existingEntity.getTenantId()); // Should not change
        assertEquals("Original Name", existingEntity.getName()); // Should not change
        assertEquals("Original Description", existingEntity.getDescription()); // Should not change
        assertEquals(new BigDecimal("5.00"), existingEntity.getPrice()); // Should not change
        assertEquals(categoryEntity, existingEntity.getCategory()); // Should not change
    }

    @Test
    void updateEntity_FromUpdateRequest_PartialUpdate() {
        // Given
        IngredientsEntity existingEntity = TestDataBuilder.IngredientsBuilder.builder()
                .id(ingredientId)
                .name("Original Name")
                .description("Original Description")
                .price(new BigDecimal("5.00"))
                .tenantId(tenantId)
                .category(categoryEntity)
                .buildEntity();

        UpdateIngredientsRequest partialRequest = new UpdateIngredientsRequest();
        partialRequest.setName("Updated Name");
        partialRequest.setPrice(new BigDecimal("15.00"));
        // description is null

        // When
        ingredientsMapper.updateEntity(partialRequest, existingEntity);

        // Then
        assertEquals(ingredientId, existingEntity.getId()); // Should not change
        assertEquals(tenantId, existingEntity.getTenantId()); // Should not change
        assertEquals("Updated Name", existingEntity.getName());
        assertEquals("Original Description", existingEntity.getDescription()); // Should not change
        assertEquals(new BigDecimal("15.00"), existingEntity.getPrice());
        assertEquals(categoryEntity, existingEntity.getCategory()); // Should not change
    }

    @Test
    void toResponse_FromEntity_WithAuditFields() {
        // Given
        UUID createdBy = UUID.randomUUID();
        UUID updatedBy = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        ingredientsEntity.setCreatedBy(createdBy);
        ingredientsEntity.setCreatedAt(createdAt);
        ingredientsEntity.setUpdatedBy(updatedBy);
        ingredientsEntity.setUpdatedAt(updatedAt);

        // When
        IngredientsResponse response = ingredientsMapper.toResponse(ingredientsEntity);

        // Then
        assertNotNull(response);
        assertEquals(ingredientId, response.getId());
        assertEquals("Test Ingredient", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals(new BigDecimal("10.50"), response.getPrice());
        assertEquals(tenantId, response.getTenantId());
        assertEquals(categoryId, response.getCategoryId());
        assertEquals("Test Category", response.getCategoryName());
        assertEquals(createdBy, response.getCreatedBy());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedBy, response.getUpdatedBy());
        assertEquals(updatedAt, response.getUpdatedAt());
    }

    @Test
    void toEntity_FromCreateRequest_WithSpecialCharacters() {
        // Given
        CreateIngredientsRequest specialRequest = new CreateIngredientsRequest();
        specialRequest.setName("Ingredient with Special Characters: @#$%^&*()");
        specialRequest.setDescription("Description with Special Characters: @#$%^&*()");
        specialRequest.setPrice(new BigDecimal("99.99"));
        specialRequest.setCategoryId(categoryId);

        // When
        IngredientsEntity entity = ingredientsMapper.toEntity(specialRequest);

        // Then
        assertNotNull(entity);
        assertEquals("Ingredient with Special Characters: @#$%^&*()", entity.getName());
        assertEquals("Description with Special Characters: @#$%^&*()", entity.getDescription());
        assertEquals(new BigDecimal("99.99"), entity.getPrice());
    }

    @Test
    void updateEntity_FromUpdateRequest_WithSpecialCharacters() {
        // Given
        IngredientsEntity existingEntity = TestDataBuilder.IngredientsBuilder.builder()
                .id(ingredientId)
                .name("Original Name")
                .description("Original Description")
                .price(new BigDecimal("5.00"))
                .tenantId(tenantId)
                .category(categoryEntity)
                .buildEntity();

        UpdateIngredientsRequest specialRequest = new UpdateIngredientsRequest();
        specialRequest.setName("Updated with Special Characters: @#$%^&*()");
        specialRequest.setDescription("Updated Description with Special Characters: @#$%^&*()");
        specialRequest.setPrice(new BigDecimal("99.99"));

        // When
        ingredientsMapper.updateEntity(specialRequest, existingEntity);

        // Then
        assertEquals("Updated with Special Characters: @#$%^&*()", existingEntity.getName());
        assertEquals("Updated Description with Special Characters: @#$%^&*()", existingEntity.getDescription());
        assertEquals(new BigDecimal("99.99"), existingEntity.getPrice());
    }

    @Test
    void toResponse_FromEntity_WithNullCategory() {
        // Given
        ingredientsEntity.setCategory(null);

        // When
        IngredientsResponse response = ingredientsMapper.toResponse(ingredientsEntity);

        // Then
        assertNotNull(response);
        assertEquals(ingredientId, response.getId());
        assertEquals("Test Ingredient", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals(new BigDecimal("10.50"), response.getPrice());
        assertEquals(tenantId, response.getTenantId());
        assertNull(response.getCategoryId());
        assertNull(response.getCategoryName());
    }

    @Test
    void toEntity_FromCreateRequest_WithZeroPrice() {
        // Given
        CreateIngredientsRequest zeroPriceRequest = new CreateIngredientsRequest();
        zeroPriceRequest.setName("Free Ingredient");
        zeroPriceRequest.setDescription("Free Description");
        zeroPriceRequest.setPrice(BigDecimal.ZERO);
        zeroPriceRequest.setCategoryId(categoryId);

        // When
        IngredientsEntity entity = ingredientsMapper.toEntity(zeroPriceRequest);

        // Then
        assertNotNull(entity);
        assertEquals("Free Ingredient", entity.getName());
        assertEquals("Free Description", entity.getDescription());
        assertEquals(BigDecimal.ZERO, entity.getPrice());
    }

    @Test
    void updateEntity_FromUpdateRequest_WithZeroPrice() {
        // Given
        IngredientsEntity existingEntity = TestDataBuilder.IngredientsBuilder.builder()
                .id(ingredientId)
                .name("Original Name")
                .description("Original Description")
                .price(new BigDecimal("5.00"))
                .tenantId(tenantId)
                .category(categoryEntity)
                .buildEntity();

        UpdateIngredientsRequest zeroPriceRequest = new UpdateIngredientsRequest();
        zeroPriceRequest.setPrice(BigDecimal.ZERO);

        // When
        ingredientsMapper.updateEntity(zeroPriceRequest, existingEntity);

        // Then
        assertEquals(BigDecimal.ZERO, existingEntity.getPrice());
    }
}
