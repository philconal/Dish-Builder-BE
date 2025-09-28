package com.conal.dishbuilder.mapper;

import com.conal.dishbuilder.domain.CategoryEntity;
import com.conal.dishbuilder.dto.CreateCategoryRequest;
import com.conal.dishbuilder.dto.UpdateCategoryRequest;
import com.conal.dishbuilder.dto.response.CategoryResponse;
import com.conal.dishbuilder.util.TestDataBuilder;
import com.conal.dishbuilder.util.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.config.import-check.enabled=false"
})
class CategoryMapperTest {

    @Autowired
    private CategoryMapper categoryMapper;

    private UUID categoryId;
    private UUID tenantId;
    private CategoryEntity categoryEntity;
    private CreateCategoryRequest createRequest;
    private UpdateCategoryRequest updateRequest;

    @BeforeEach
    void setUp() {
        categoryId = TestConstants.TEST_CATEGORY_ID;
        tenantId = TestConstants.TEST_TENANT_ID;
        
        categoryEntity = TestDataBuilder.CategoryBuilder.builder()
                .id(categoryId)
                .name("Test Category")
                .description("Test Description")
                .tenantId(tenantId)
                .buildEntity();
        
        createRequest = TestDataBuilder.CategoryBuilder.builder()
                .name("New Category")
                .description("New Description")
                .buildCreateRequest();
        
        updateRequest = TestDataBuilder.CategoryBuilder.builder()
                .name("Updated Category")
                .description("Updated Description")
                .buildUpdateRequest();
    }

    @Test
    void toEntity_FromCreateRequest_Success() {
        // When
        CategoryEntity entity = categoryMapper.toEntity(createRequest);

        // Then
        assertNotNull(entity);
        assertNull(entity.getId()); // Should be null for new entity
        assertNull(entity.getTenantId()); // Should be null, will be set by service
        assertEquals("New Category", entity.getName());
        assertEquals("New Description", entity.getDescription());
        assertNull(entity.getIngredients()); // Should be null for new entity
    }

    @Test
    void toEntity_FromCreateRequest_NullValues() {
        // Given
        CreateCategoryRequest nullRequest = new CreateCategoryRequest();
        nullRequest.setName(null);
        nullRequest.setDescription(null);

        // When
        CategoryEntity entity = categoryMapper.toEntity(nullRequest);

        // Then
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getTenantId());
        assertNull(entity.getName());
        assertNull(entity.getDescription());
        assertNull(entity.getIngredients());
    }

    @Test
    void toResponse_FromEntity_Success() {
        // Given
        categoryEntity.setCreatedBy(tenantId);
        categoryEntity.setCreatedAt(LocalDateTime.now());
        categoryEntity.setUpdatedBy(tenantId);
        categoryEntity.setUpdatedAt(LocalDateTime.now());

        // When
        CategoryResponse response = categoryMapper.toResponse(categoryEntity);

        // Then
        assertNotNull(response);
        assertEquals(categoryId, response.getId());
        assertEquals("Test Category", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals(tenantId, response.getTenantId());
        assertEquals(tenantId, response.getCreatedBy());
        assertNotNull(response.getCreatedAt());
        assertEquals(tenantId, response.getUpdatedBy());
        assertNotNull(response.getUpdatedAt());
    }

    @Test
    void toResponse_FromEntity_NullValues() {
        // Given
        CategoryEntity nullEntity = new CategoryEntity();
        nullEntity.setId(categoryId);
        nullEntity.setName(null);
        nullEntity.setDescription(null);
        nullEntity.setTenantId(null);

        // When
        CategoryResponse response = categoryMapper.toResponse(nullEntity);

        // Then
        assertNotNull(response);
        assertEquals(categoryId, response.getId());
        assertNull(response.getName());
        assertNull(response.getDescription());
        assertNull(response.getTenantId());
        assertNull(response.getCreatedBy());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedBy());
        assertNull(response.getUpdatedAt());
    }

    @Test
    void updateEntity_FromUpdateRequest_Success() {
        // Given
        CategoryEntity existingEntity = TestDataBuilder.CategoryBuilder.builder()
                .id(categoryId)
                .name("Original Name")
                .description("Original Description")
                .tenantId(tenantId)
                .buildEntity();

        // When
        categoryMapper.updateEntity(updateRequest, existingEntity);

        // Then
        assertEquals(categoryId, existingEntity.getId()); // Should not change
        assertEquals(tenantId, existingEntity.getTenantId()); // Should not change
        assertEquals("Updated Category", existingEntity.getName());
        assertEquals("Updated Description", existingEntity.getDescription());
    }

    @Test
    void updateEntity_FromUpdateRequest_NullValues() {
        // Given
        CategoryEntity existingEntity = TestDataBuilder.CategoryBuilder.builder()
                .id(categoryId)
                .name("Original Name")
                .description("Original Description")
                .tenantId(tenantId)
                .buildEntity();

        UpdateCategoryRequest nullRequest = new UpdateCategoryRequest();
        nullRequest.setName(null);
        nullRequest.setDescription(null);

        // When
        categoryMapper.updateEntity(nullRequest, existingEntity);

        // Then
        assertEquals(categoryId, existingEntity.getId()); // Should not change
        assertEquals(tenantId, existingEntity.getTenantId()); // Should not change
        assertEquals("Original Name", existingEntity.getName()); // Should not change
        assertEquals("Original Description", existingEntity.getDescription()); // Should not change
    }

    @Test
    void updateEntity_FromUpdateRequest_PartialUpdate() {
        // Given
        CategoryEntity existingEntity = TestDataBuilder.CategoryBuilder.builder()
                .id(categoryId)
                .name("Original Name")
                .description("Original Description")
                .tenantId(tenantId)
                .buildEntity();

        UpdateCategoryRequest partialRequest = new UpdateCategoryRequest();
        partialRequest.setName("Updated Name");
        // description is null

        // When
        categoryMapper.updateEntity(partialRequest, existingEntity);

        // Then
        assertEquals(categoryId, existingEntity.getId()); // Should not change
        assertEquals(tenantId, existingEntity.getTenantId()); // Should not change
        assertEquals("Updated Name", existingEntity.getName());
        assertEquals("Original Description", existingEntity.getDescription()); // Should not change
    }

    @Test
    void toResponse_FromEntity_WithAuditFields() {
        // Given
        UUID createdBy = UUID.randomUUID();
        UUID updatedBy = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        categoryEntity.setCreatedBy(createdBy);
        categoryEntity.setCreatedAt(createdAt);
        categoryEntity.setUpdatedBy(updatedBy);
        categoryEntity.setUpdatedAt(updatedAt);

        // When
        CategoryResponse response = categoryMapper.toResponse(categoryEntity);

        // Then
        assertNotNull(response);
        assertEquals(categoryId, response.getId());
        assertEquals("Test Category", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals(tenantId, response.getTenantId());
        assertEquals(createdBy, response.getCreatedBy());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedBy, response.getUpdatedBy());
        assertEquals(updatedAt, response.getUpdatedAt());
    }

    @Test
    void toEntity_FromCreateRequest_WithSpecialCharacters() {
        // Given
        CreateCategoryRequest specialRequest = new CreateCategoryRequest();
        specialRequest.setName("Category with Special Characters: @#$%^&*()");
        specialRequest.setDescription("Description with Special Characters: @#$%^&*()");

        // When
        CategoryEntity entity = categoryMapper.toEntity(specialRequest);

        // Then
        assertNotNull(entity);
        assertEquals("Category with Special Characters: @#$%^&*()", entity.getName());
        assertEquals("Description with Special Characters: @#$%^&*()", entity.getDescription());
    }

    @Test
    void updateEntity_FromUpdateRequest_WithSpecialCharacters() {
        // Given
        CategoryEntity existingEntity = TestDataBuilder.CategoryBuilder.builder()
                .id(categoryId)
                .name("Original Name")
                .description("Original Description")
                .tenantId(tenantId)
                .buildEntity();

        UpdateCategoryRequest specialRequest = new UpdateCategoryRequest();
        specialRequest.setName("Updated with Special Characters: @#$%^&*()");
        specialRequest.setDescription("Updated Description with Special Characters: @#$%^&*()");

        // When
        categoryMapper.updateEntity(specialRequest, existingEntity);

        // Then
        assertEquals("Updated with Special Characters: @#$%^&*()", existingEntity.getName());
        assertEquals("Updated Description with Special Characters: @#$%^&*()", existingEntity.getDescription());
    }
}
