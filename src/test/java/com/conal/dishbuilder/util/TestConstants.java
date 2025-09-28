package com.conal.dishbuilder.util;

import java.util.UUID;

public class TestConstants {
    
    // Test UUIDs
    public static final UUID TEST_TENANT_ID = UUID.fromString("7cccdad4-c562-402b-8dce-d64559a91500");
    public static final UUID TEST_CATEGORY_ID = UUID.fromString("660e8400-e29b-41d4-a716-446655440001");
    public static final UUID TEST_INGREDIENT_ID = UUID.fromString("880e8400-e29b-41d4-a716-446655440001");
    public static final UUID TEST_INGREDIENT_ID_1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    public static final UUID TEST_INGREDIENT_ID_2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    public static final UUID TEST_USER_ID = UUID.fromString("770e8400-e29b-41d4-a716-446655440001");
    public static final UUID TEST_DISH_ID = UUID.fromString("990e8400-e29b-41d4-a716-446655440001");
    
    // Test Names
    public static final String TEST_CATEGORY_NAME = "Test Category";
    public static final String TEST_INGREDIENT_NAME = "Test Ingredient";
    public static final String TEST_DISH_NAME = "Test Dish";
    public static final String TEST_USERNAME = "testuser";
    
    // Test Descriptions
    public static final String TEST_CATEGORY_DESCRIPTION = "Test Category Description";
    public static final String TEST_INGREDIENT_DESCRIPTION = "Test Ingredient Description";
    public static final String TEST_DISH_DESCRIPTION = "Test Dish Description";
    
    // Test Prices
    public static final String TEST_INGREDIENT_PRICE = "10.50";
    public static final String TEST_DISH_TOTAL_PRICE = "15.00";
    public static final String TEST_DISH_DISCOUNT = "2.00";
    public static final String TEST_DISH_VAT = "1.30";
    
    // Error Messages
    public static final String NOT_FOUND_MESSAGE = "not found with id:";
    public static final String VALIDATION_ERROR_MESSAGE = "Name already exists";
    public static final String TENANT_ACCESS_ERROR = "does not belong to current tenant";
    
    // Pagination
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 10;
    public static final String DEFAULT_SORT_BY = "name";
    public static final String DEFAULT_SORT_DIRECTION = "ASC";
}
