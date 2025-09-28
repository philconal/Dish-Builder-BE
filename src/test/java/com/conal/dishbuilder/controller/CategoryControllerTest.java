package com.conal.dishbuilder.controller;

import com.conal.dishbuilder.dto.CreateCategoryRequest;
import com.conal.dishbuilder.dto.UpdateCategoryRequest;
import com.conal.dishbuilder.dto.request.filter.CategoryFilterRequest;
import com.conal.dishbuilder.dto.response.BaseResponse;
import com.conal.dishbuilder.dto.response.CategoryResponse;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.service.CategoryService;
import com.conal.dishbuilder.util.TestDataBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@org.springframework.test.context.TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.config.import-check.enabled=false"
})
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID categoryId;
    private CategoryResponse categoryResponse;
    private CreateCategoryRequest createRequest;
    private UpdateCategoryRequest updateRequest;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        
        categoryResponse = TestDataBuilder.CategoryBuilder.builder()
                .id(categoryId)
                .name("Test Category")
                .description("Test Description")
                .buildResponse();
                
        createRequest = TestDataBuilder.CategoryBuilder.builder()
                .name("Test Category")
                .description("Test Description")
                .buildCreateRequest();
                
        updateRequest = TestDataBuilder.CategoryBuilder.builder()
                .name("Updated Category")
                .description("Updated Description")
                .buildUpdateRequest();
    }

    @Test
    void addCategory_Success() throws Exception {
        // Given
        when(categoryService.addCategory(any(CreateCategoryRequest.class)))
                .thenReturn(categoryResponse);

        // When & Then
        mockMvc.perform(post("/v1.0/category/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(categoryId.toString()))
                .andExpect(jsonPath("$.data.name").value("Test Category"))
                .andExpect(jsonPath("$.data.description").value("Test Description"));
    }

    @Test
    void addCategory_ValidationError() throws Exception {
        // Given
        createRequest.setName(""); // Invalid name

        // When & Then
        mockMvc.perform(post("/v1.0/category/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCategoryById_Success() throws Exception {
        // Given
        when(categoryService.getCategoryById(categoryId))
                .thenReturn(categoryResponse);

        // When & Then
        mockMvc.perform(get("/v1.0/category/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(categoryId.toString()))
                .andExpect(jsonPath("$.data.name").value("Test Category"))
                .andExpect(jsonPath("$.data.description").value("Test Description"));
    }

    @Test
    void getCategories_Success() throws Exception {
        // Given
        PageResponse<CategoryResponse> pageResponse = PageResponse.<CategoryResponse>builder()
                .data(Arrays.asList(categoryResponse))
                .totalElements(1L)
                .totalPages(1)
                .size(10)
                .page(0)
                .build();

        when(categoryService.getCategories(any(CategoryFilterRequest.class)))
                .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/v1.0/category/")
                        .param("name", "Test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.data").isArray())
                .andExpect(jsonPath("$.data.data[0].id").value(categoryId.toString()))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1));
    }

    @Test
    void updateCategory_Success() throws Exception {
        // Given
        when(categoryService.updateCategory(eq(categoryId), any(UpdateCategoryRequest.class)))
                .thenReturn(categoryResponse);

        // When & Then
        mockMvc.perform(put("/v1.0/category/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(categoryId.toString()))
                .andExpect(jsonPath("$.data.name").value("Test Category"))
                .andExpect(jsonPath("$.data.description").value("Test Description"));
    }

    @Test
    void updateCategory_ValidationError() throws Exception {
        // Given
        updateRequest.setName(""); // Invalid name

        // When & Then
        mockMvc.perform(put("/v1.0/category/{id}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCategory_Success() throws Exception {
        // Given
        when(categoryService.deleteCategory(categoryId))
                .thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/v1.0/category/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }
}
