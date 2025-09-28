package com.conal.dishbuilder.controller;

import com.conal.dishbuilder.dto.CreateIngredientsRequest;
import com.conal.dishbuilder.dto.UpdateIngredientsRequest;
import com.conal.dishbuilder.dto.request.filter.IngredientsFilterRequest;
import com.conal.dishbuilder.dto.response.BaseResponse;
import com.conal.dishbuilder.dto.response.IngredientsResponse;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.service.IngredientsService;
import com.conal.dishbuilder.util.TestDataBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IngredientsController.class)
@org.springframework.test.context.TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.config.import-check.enabled=false"
})
class IngredientsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IngredientsService ingredientsService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID ingredientId;
    private UUID categoryId;
    private IngredientsResponse ingredientsResponse;
    private CreateIngredientsRequest createRequest;
    private UpdateIngredientsRequest updateRequest;

    @BeforeEach
    void setUp() {
        ingredientId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        
        ingredientsResponse = TestDataBuilder.IngredientsBuilder.builder()
                .id(ingredientId)
                .name("Test Ingredient")
                .description("Test Ingredient Description")
                .price(new BigDecimal("10.50"))
                .categoryId(categoryId)
                .buildResponse();
        ingredientsResponse.setCategoryName("Test Category");
                
        createRequest = TestDataBuilder.IngredientsBuilder.builder()
                .name("Test Ingredient")
                .description("Test Ingredient Description")
                .price(new BigDecimal("10.50"))
                .categoryId(categoryId)
                .buildCreateRequest();
                
        updateRequest = TestDataBuilder.IngredientsBuilder.builder()
                .name("Updated Ingredient")
                .description("Updated Ingredient Description")
                .price(new BigDecimal("12.00"))
                .categoryId(categoryId)
                .buildUpdateRequest();
    }

    @Test
    void addIngredients_Success() throws Exception {
        // Given
        when(ingredientsService.addIngredients(any(CreateIngredientsRequest.class)))
                .thenReturn(ingredientsResponse);

        // When & Then
        mockMvc.perform(post("/v1.0/ingredients/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(ingredientId.toString()))
                .andExpect(jsonPath("$.data.name").value("Test Ingredient"))
                .andExpect(jsonPath("$.data.description").value("Test Ingredient Description"))
                .andExpect(jsonPath("$.data.price").value(10.50))
                .andExpect(jsonPath("$.data.categoryId").value(categoryId.toString()))
                .andExpect(jsonPath("$.data.categoryName").value("Test Category"));
    }

    @Test
    void addIngredients_ValidationError() throws Exception {
        // Given
        createRequest.setName(""); // Invalid name

        // When & Then
        mockMvc.perform(post("/v1.0/ingredients/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addIngredients_MissingRequiredFields() throws Exception {
        // Given
        createRequest.setName(null);
        createRequest.setDescription(null);
        createRequest.setPrice(null);
        createRequest.setCategoryId(null);

        // When & Then
        mockMvc.perform(post("/v1.0/ingredients/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getIngredientsById_Success() throws Exception {
        // Given
        when(ingredientsService.getIngredientsById(ingredientId))
                .thenReturn(ingredientsResponse);

        // When & Then
        mockMvc.perform(get("/v1.0/ingredients/{id}", ingredientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(ingredientId.toString()))
                .andExpect(jsonPath("$.data.name").value("Test Ingredient"))
                .andExpect(jsonPath("$.data.description").value("Test Ingredient Description"))
                .andExpect(jsonPath("$.data.price").value(10.50))
                .andExpect(jsonPath("$.data.categoryId").value(categoryId.toString()))
                .andExpect(jsonPath("$.data.categoryName").value("Test Category"));
    }

    @Test
    void getIngredients_Success() throws Exception {
        // Given
        PageResponse<IngredientsResponse> pageResponse = PageResponse.<IngredientsResponse>builder()
                .data(Arrays.asList(ingredientsResponse))
                .totalElements(1L)
                .totalPages(1)
                .size(10)
                .page(0)
                .build();

        when(ingredientsService.getIngredients(any(IngredientsFilterRequest.class)))
                .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/v1.0/ingredients/")
                        .param("name", "Test")
                        .param("categoryId", categoryId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.data").isArray())
                .andExpect(jsonPath("$.data.data[0].id").value(ingredientId.toString()))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1));
    }

    @Test
    void getIngredients_WithFilters() throws Exception {
        // Given
        PageResponse<IngredientsResponse> pageResponse = PageResponse.<IngredientsResponse>builder()
                .data(Arrays.asList(ingredientsResponse))
                .totalElements(1L)
                .totalPages(1)
                .size(10)
                .page(0)
                .build();

        when(ingredientsService.getIngredients(any(IngredientsFilterRequest.class)))
                .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/v1.0/ingredients/")
                        .param("name", "Test")
                        .param("description", "Description")
                        .param("minPrice", "5.00")
                        .param("maxPrice", "15.00")
                        .param("categoryId", categoryId.toString())
                        .param("categoryName", "Test Category")
                        .param("sortBy", "name")
                        .param("sortDirection", "ASC")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.data").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void updateIngredients_Success() throws Exception {
        // Given
        when(ingredientsService.updateIngredients(eq(ingredientId), any(UpdateIngredientsRequest.class)))
                .thenReturn(ingredientsResponse);

        // When & Then
        mockMvc.perform(put("/v1.0/ingredients/{id}", ingredientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(ingredientId.toString()))
                .andExpect(jsonPath("$.data.name").value("Test Ingredient"))
                .andExpect(jsonPath("$.data.description").value("Test Ingredient Description"));
    }

    @Test
    void updateIngredients_ValidationError() throws Exception {
        // Given
        updateRequest.setName(""); // Invalid name

        // When & Then
        mockMvc.perform(put("/v1.0/ingredients/{id}", ingredientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateIngredients_InvalidPrice() throws Exception {
        // Given
        updateRequest.setPrice(new BigDecimal("-1.00")); // Invalid price

        // When & Then
        mockMvc.perform(put("/v1.0/ingredients/{id}", ingredientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteIngredients_Success() throws Exception {
        // Given
        when(ingredientsService.deleteIngredients(ingredientId))
                .thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/v1.0/ingredients/{id}", ingredientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void deleteIngredients_NotFound() throws Exception {
        // Given
        when(ingredientsService.deleteIngredients(ingredientId))
                .thenThrow(new com.conal.dishbuilder.exception.NotFoundException("Ingredients not found with id: " + ingredientId));

        // When & Then
        mockMvc.perform(delete("/v1.0/ingredients/{id}", ingredientId))
                .andExpect(status().isNotFound());
    }

    @Test
    void addIngredients_InvalidJson() throws Exception {
        // Given
        String invalidJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(post("/v1.0/ingredients/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateIngredients_InvalidJson() throws Exception {
        // Given
        String invalidJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(put("/v1.0/ingredients/{id}", ingredientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
