package com.conal.dishbuilder.controller;

import com.conal.dishbuilder.dto.CreateDishRequest;
import com.conal.dishbuilder.dto.UpdateDishRequest;
import com.conal.dishbuilder.dto.request.filter.DishFilterRequest;
import com.conal.dishbuilder.dto.response.BaseResponse;
import com.conal.dishbuilder.dto.response.DishResponse;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.service.DishService;
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

@WebMvcTest(DishController.class)
@org.springframework.test.context.TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.config.import-check.enabled=false"
})
class DishControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DishService dishService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID dishId;
    private UUID userId;
    private UUID ingredientId1;
    private UUID ingredientId2;
    private DishResponse dishResponse;
    private CreateDishRequest createRequest;
    private UpdateDishRequest updateRequest;

    @BeforeEach
    void setUp() {
        dishId = UUID.randomUUID();
        userId = UUID.randomUUID();
        ingredientId1 = UUID.randomUUID();
        ingredientId2 = UUID.randomUUID();
        
        dishResponse = TestDataBuilder.DishBuilder.builder()
                .id(dishId)
                .name("Test Dish")
                .description("Test Dish Description")
                .totalPrice(new BigDecimal("15.00"))
                .discount(new BigDecimal("2.00"))
                .vat(new BigDecimal("1.30"))
                .userId(userId)
                .buildResponse();
        dishResponse.setUserName("testuser");
                
        createRequest = TestDataBuilder.DishBuilder.builder()
                .name("Test Dish")
                .description("Test Dish Description")
                .totalPrice(new BigDecimal("15.00"))
                .discount(new BigDecimal("2.00"))
                .vat(new BigDecimal("1.30"))
                .userId(userId)
                .ingredientIds(Arrays.asList(ingredientId1, ingredientId2))
                .buildCreateRequest();
                
        updateRequest = TestDataBuilder.DishBuilder.builder()
                .name("Updated Dish")
                .description("Updated Dish Description")
                .totalPrice(new BigDecimal("18.00"))
                .discount(new BigDecimal("3.00"))
                .vat(new BigDecimal("1.50"))
                .userId(userId)
                .ingredientIds(Arrays.asList(ingredientId1))
                .buildUpdateRequest();
    }

    @Test
    void addDish_Success() throws Exception {
        // Given
        when(dishService.addDish(any(CreateDishRequest.class)))
                .thenReturn(dishResponse);

        // When & Then
        mockMvc.perform(post("/v1.0/dish/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(dishId.toString()))
                .andExpect(jsonPath("$.data.name").value("Test Dish"))
                .andExpect(jsonPath("$.data.description").value("Test Dish Description"))
                .andExpect(jsonPath("$.data.totalPrice").value(15.00))
                .andExpect(jsonPath("$.data.discount").value(2.00))
                .andExpect(jsonPath("$.data.vat").value(1.30))
                .andExpect(jsonPath("$.data.userId").value(userId.toString()))
                .andExpect(jsonPath("$.data.userName").value("testuser"));
    }

    @Test
    void addDish_ValidationError() throws Exception {
        // Given
        createRequest.setName(""); // Invalid name

        // When & Then
        mockMvc.perform(post("/v1.0/dish/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addDish_MissingRequiredFields() throws Exception {
        // Given
        createRequest.setName(null);
        createRequest.setDescription(null);
        createRequest.setUserId(null);
        createRequest.setIngredientIds(null);

        // When & Then
        mockMvc.perform(post("/v1.0/dish/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addDish_InvalidPrice() throws Exception {
        // Given
        createRequest.setTotalPrice(new BigDecimal("-1.00")); // Invalid price

        // When & Then
        mockMvc.perform(post("/v1.0/dish/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addDish_EmptyIngredientList() throws Exception {
        // Given
        createRequest.setIngredientIds(Arrays.asList()); // Empty list

        // When & Then
        mockMvc.perform(post("/v1.0/dish/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDishById_Success() throws Exception {
        // Given
        when(dishService.getDishById(dishId))
                .thenReturn(dishResponse);

        // When & Then
        mockMvc.perform(get("/v1.0/dish/{id}", dishId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(dishId.toString()))
                .andExpect(jsonPath("$.data.name").value("Test Dish"))
                .andExpect(jsonPath("$.data.description").value("Test Dish Description"))
                .andExpect(jsonPath("$.data.totalPrice").value(15.00))
                .andExpect(jsonPath("$.data.discount").value(2.00))
                .andExpect(jsonPath("$.data.vat").value(1.30))
                .andExpect(jsonPath("$.data.userId").value(userId.toString()))
                .andExpect(jsonPath("$.data.userName").value("testuser"));
    }

    @Test
    void getDishes_Success() throws Exception {
        // Given
        PageResponse<DishResponse> pageResponse = PageResponse.<DishResponse>builder()
                .data(Arrays.asList(dishResponse))
                .totalElements(1L)
                .totalPages(1)
                .size(10)
                .page(0)
                .build();

        when(dishService.getDishes(any(DishFilterRequest.class)))
                .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/v1.0/dish/")
                        .param("name", "Test")
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.data").isArray())
                .andExpect(jsonPath("$.data.data[0].id").value(dishId.toString()))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1));
    }

    @Test
    void getDishes_WithFilters() throws Exception {
        // Given
        PageResponse<DishResponse> pageResponse = PageResponse.<DishResponse>builder()
                .data(Arrays.asList(dishResponse))
                .totalElements(1L)
                .totalPages(1)
                .size(10)
                .page(0)
                .build();

        when(dishService.getDishes(any(DishFilterRequest.class)))
                .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/v1.0/dish/")
                        .param("name", "Test")
                        .param("description", "Description")
                        .param("minTotalPrice", "10.00")
                        .param("maxTotalPrice", "20.00")
                        .param("minDiscount", "1.00")
                        .param("maxDiscount", "5.00")
                        .param("userId", userId.toString())
                        .param("userName", "testuser")
                        .param("ingredientId", ingredientId1.toString())
                        .param("ingredientName", "Ingredient")
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
    void updateDish_Success() throws Exception {
        // Given
        when(dishService.updateDish(eq(dishId), any(UpdateDishRequest.class)))
                .thenReturn(dishResponse);

        // When & Then
        mockMvc.perform(put("/v1.0/dish/{id}", dishId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(dishId.toString()))
                .andExpect(jsonPath("$.data.name").value("Test Dish"))
                .andExpect(jsonPath("$.data.description").value("Test Dish Description"));
    }

    @Test
    void updateDish_ValidationError() throws Exception {
        // Given
        updateRequest.setName(""); // Invalid name

        // When & Then
        mockMvc.perform(put("/v1.0/dish/{id}", dishId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateDish_InvalidPrice() throws Exception {
        // Given
        updateRequest.setTotalPrice(new BigDecimal("-1.00")); // Invalid price

        // When & Then
        mockMvc.perform(put("/v1.0/dish/{id}", dishId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateDish_InvalidDiscount() throws Exception {
        // Given
        updateRequest.setDiscount(new BigDecimal("-1.00")); // Invalid discount

        // When & Then
        mockMvc.perform(put("/v1.0/dish/{id}", dishId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateDish_InvalidVat() throws Exception {
        // Given
        updateRequest.setVat(new BigDecimal("-1.00")); // Invalid VAT

        // When & Then
        mockMvc.perform(put("/v1.0/dish/{id}", dishId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteDish_Success() throws Exception {
        // Given
        when(dishService.deleteDish(dishId))
                .thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/v1.0/dish/{id}", dishId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void deleteDish_NotFound() throws Exception {
        // Given
        when(dishService.deleteDish(dishId))
                .thenThrow(new com.conal.dishbuilder.exception.NotFoundException("Dish not found with id: " + dishId));

        // When & Then
        mockMvc.perform(delete("/v1.0/dish/{id}", dishId))
                .andExpect(status().isNotFound());
    }

    @Test
    void addDish_InvalidJson() throws Exception {
        // Given
        String invalidJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(post("/v1.0/dish/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateDish_InvalidJson() throws Exception {
        // Given
        String invalidJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(put("/v1.0/dish/{id}", dishId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDishById_NotFound() throws Exception {
        // Given
        when(dishService.getDishById(dishId))
                .thenThrow(new com.conal.dishbuilder.exception.NotFoundException("Dish not found with id: " + dishId));

        // When & Then
        mockMvc.perform(get("/v1.0/dish/{id}", dishId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDishes_EmptyResult() throws Exception {
        // Given
        PageResponse<DishResponse> pageResponse = PageResponse.<DishResponse>builder()
                .data(Arrays.asList())
                .totalElements(0L)
                .totalPages(0)
                .size(10)
                .page(0)
                .build();

        when(dishService.getDishes(any(DishFilterRequest.class)))
                .thenReturn(pageResponse);

        // When & Then
        mockMvc.perform(get("/v1.0/dish/")
                        .param("name", "NonExistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.data").isArray())
                .andExpect(jsonPath("$.data.data").isEmpty())
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }
}
