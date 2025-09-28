package com.conal.dishbuilder.integration;

import com.conal.dishbuilder.domain.CategoryEntity;
import com.conal.dishbuilder.dto.CreateCategoryRequest;
import com.conal.dishbuilder.dto.UpdateCategoryRequest;
import com.conal.dishbuilder.dto.request.filter.CategoryFilterRequest;
import com.conal.dishbuilder.dto.response.CategoryResponse;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.repository.CategoryRepository;
import com.conal.dishbuilder.service.CategoryService;
import com.conal.dishbuilder.util.TestDataBuilder;
import com.conal.dishbuilder.util.TestConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.config.import-check.enabled=false",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class CategoryIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private UUID tenantId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        tenantId = TestConstants.TEST_TENANT_ID;
        
        // Set tenant context for tests
        com.conal.dishbuilder.context.TenantContextHolder.setTenantContext(tenantId);
    }

    @Test
    void createCategory_Integration_Success() throws Exception {
        // Given
        CreateCategoryRequest request = TestDataBuilder.CategoryBuilder.builder()
                .name("Integration Test Category")
                .description("Integration Test Description")
                .buildCreateRequest();

        // When & Then
        mockMvc.perform(post("/v1.0/category/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Integration Test Category"))
                .andExpect(jsonPath("$.data.description").value("Integration Test Description"))
                .andExpect(jsonPath("$.data.tenantId").value(tenantId.toString()));

        // Verify in database
        var categories = categoryRepository.findAll();
        assertTrue(categories.stream().anyMatch(c -> c.getName().equals("Integration Test Category")));
    }

    @Test
    void getCategoryById_Integration_Success() throws Exception {
        // Given
        CategoryEntity category = TestDataBuilder.CategoryBuilder.builder()
                .name("Test Category")
                .description("Test Description")
                .tenantId(tenantId)
                .buildEntity();
        CategoryEntity saved = categoryRepository.save(category);

        // When & Then
        mockMvc.perform(get("/v1.0/category/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.data.name").value("Test Category"))
                .andExpect(jsonPath("$.data.description").value("Test Description"))
                .andExpect(jsonPath("$.data.tenantId").value(tenantId.toString()));
    }

    @Test
    void getCategories_Integration_Success() throws Exception {
        // Given
        CategoryEntity category1 = TestDataBuilder.CategoryBuilder.builder()
                .name("Category 1")
                .description("Description 1")
                .tenantId(tenantId)
                .buildEntity();
        
        CategoryEntity category2 = TestDataBuilder.CategoryBuilder.builder()
                .name("Category 2")
                .description("Description 2")
                .tenantId(tenantId)
                .buildEntity();

        categoryRepository.save(category1);
        categoryRepository.save(category2);

        // When & Then
        mockMvc.perform(get("/v1.0/category/")
                        .param("name", "Category")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.data").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(1));
    }

    @Test
    void updateCategory_Integration_Success() throws Exception {
        // Given
        CategoryEntity category = TestDataBuilder.CategoryBuilder.builder()
                .name("Original Category")
                .description("Original Description")
                .tenantId(tenantId)
                .buildEntity();
        CategoryEntity saved = categoryRepository.save(category);

        UpdateCategoryRequest request = TestDataBuilder.CategoryBuilder.builder()
                .name("Updated Category")
                .description("Updated Description")
                .buildUpdateRequest();

        // When & Then
        mockMvc.perform(put("/v1.0/category/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.data.name").value("Updated Category"))
                .andExpect(jsonPath("$.data.description").value("Updated Description"));

        // Verify in database
        var updated = categoryRepository.findById(saved.getId());
        assertTrue(updated.isPresent());
        assertEquals("Updated Category", updated.get().getName());
        assertEquals("Updated Description", updated.get().getDescription());
    }

    @Test
    void deleteCategory_Integration_Success() throws Exception {
        // Given
        CategoryEntity category = TestDataBuilder.CategoryBuilder.builder()
                .name("To Delete Category")
                .description("To Delete Description")
                .tenantId(tenantId)
                .buildEntity();
        CategoryEntity saved = categoryRepository.save(category);

        // When & Then
        mockMvc.perform(delete("/v1.0/category/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));

        // Verify in database
        var deleted = categoryRepository.findById(saved.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void createCategory_Integration_ValidationError() throws Exception {
        // Given
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName(""); // Invalid name
        request.setDescription("Valid Description");

        // When & Then
        mockMvc.perform(post("/v1.0/category/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCategoryById_Integration_NotFound() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(get("/v1.0/category/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCategory_Integration_NotFound() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        UpdateCategoryRequest request = TestDataBuilder.CategoryBuilder.builder()
                .name("Updated Category")
                .description("Updated Description")
                .buildUpdateRequest();

        // When & Then
        mockMvc.perform(put("/v1.0/category/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCategory_Integration_NotFound() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(delete("/v1.0/category/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCategory_Integration_DuplicateName() throws Exception {
        // Given
        CategoryEntity existing = TestDataBuilder.CategoryBuilder.builder()
                .name("Duplicate Category")
                .description("Existing Description")
                .tenantId(tenantId)
                .buildEntity();
        categoryRepository.save(existing);

        CreateCategoryRequest request = TestDataBuilder.CategoryBuilder.builder()
                .name("Duplicate Category")
                .description("New Description")
                .buildCreateRequest();

        // When & Then
        mockMvc.perform(post("/v1.0/category/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCategories_Integration_WithFilters() throws Exception {
        // Given
        CategoryEntity category1 = TestDataBuilder.CategoryBuilder.builder()
                .name("Vegetable Category")
                .description("Fresh vegetables")
                .tenantId(tenantId)
                .buildEntity();
        
        CategoryEntity category2 = TestDataBuilder.CategoryBuilder.builder()
                .name("Meat Category")
                .description("Fresh meat")
                .tenantId(tenantId)
                .buildEntity();

        categoryRepository.save(category1);
        categoryRepository.save(category2);

        // When & Then
        mockMvc.perform(get("/v1.0/category/")
                        .param("name", "Vegetable")
                        .param("description", "Fresh")
                        .param("sortBy", "name")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.data").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.data[0].name").value("Vegetable Category"));
    }

    @Test
    void updateCategory_Integration_DuplicateName() throws Exception {
        // Given
        CategoryEntity existing1 = TestDataBuilder.CategoryBuilder.builder()
                .name("Existing Category 1")
                .description("Description 1")
                .tenantId(tenantId)
                .buildEntity();
        
        CategoryEntity existing2 = TestDataBuilder.CategoryBuilder.builder()
                .name("Existing Category 2")
                .description("Description 2")
                .tenantId(tenantId)
                .buildEntity();

        CategoryEntity saved1 = categoryRepository.save(existing1);
        categoryRepository.save(existing2);

        UpdateCategoryRequest request = TestDataBuilder.CategoryBuilder.builder()
                .name("Existing Category 2") // Duplicate name
                .description("Updated Description")
                .buildUpdateRequest();

        // When & Then
        mockMvc.perform(put("/v1.0/category/{id}", saved1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
