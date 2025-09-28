# API Testing Commands for Dish Builder

## Prerequisites
1. Start the Spring Boot application on `localhost:8080`
2. Get JWT token by logging in with superadmin credentials

## 1. Login to Get JWT Token

```bash
curl -X POST "http://localhost:8080/v1.0/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "superadmin",
    "password": "admin123"
  }'
```

**Response:** Copy the `token` value from the response for use in subsequent requests.

---

## 2. Category APIs

### 2.1 Get All Categories
```bash
curl -X GET "http://localhost:8080/v1.0/category/" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 2.2 Create New Category
```bash
curl -X POST "http://localhost:8080/v1.0/category/" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Category",
    "description": "This is a test category created via API"
  }'
```

### 2.3 Get Category by ID
```bash
curl -X GET "http://localhost:8080/v1.0/category/CATEGORY_ID" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 2.4 Update Category
```bash
curl -X PUT "http://localhost:8080/v1.0/category/CATEGORY_ID" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Test Category",
    "description": "This category has been updated via API"
  }'
```

### 2.5 Delete Category
```bash
curl -X DELETE "http://localhost:8080/v1.0/category/CATEGORY_ID" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 2.6 Filter Categories
```bash
# Filter by name
curl -X GET "http://localhost:8080/v1.0/category/?name=Vegetables&page=0&size=5" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"

# Filter by description
curl -X GET "http://localhost:8080/v1.0/category/?description=fresh&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"

# Sort by name descending
curl -X GET "http://localhost:8080/v1.0/category/?sortBy=name&sortDirection=DESC&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

---

## 3. Ingredients APIs

### 3.1 Get All Ingredients
```bash
curl -X GET "http://localhost:8080/v1.0/ingredients/" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 3.2 Create New Ingredient
```bash
curl -X POST "http://localhost:8080/v1.0/ingredients/" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Ingredient",
    "description": "This is a test ingredient created via API",
    "price": 5.50,
    "categoryId": "660e8400-e29b-41d4-a716-446655440001"
  }'
```

### 3.3 Get Ingredient by ID
```bash
curl -X GET "http://localhost:8080/v1.0/ingredients/INGREDIENT_ID" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 3.4 Update Ingredient
```bash
curl -X PUT "http://localhost:8080/v1.0/ingredients/INGREDIENT_ID" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Test Ingredient",
    "description": "This ingredient has been updated via API",
    "price": 7.25
  }'
```

### 3.5 Delete Ingredient
```bash
curl -X DELETE "http://localhost:8080/v1.0/ingredients/INGREDIENT_ID" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 3.6 Filter Ingredients
```bash
# Filter by name
curl -X GET "http://localhost:8080/v1.0/ingredients/?name=Tomato&page=0&size=5" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"

# Filter by price range
curl -X GET "http://localhost:8080/v1.0/ingredients/?minPrice=5&maxPrice=15&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"

# Filter by category
curl -X GET "http://localhost:8080/v1.0/ingredients/?categoryId=660e8400-e29b-41d4-a716-446655440001&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"

# Filter by category name
curl -X GET "http://localhost:8080/v1.0/ingredients/?categoryName=Vegetables&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"

# Complex filtering
curl -X GET "http://localhost:8080/v1.0/ingredients/?name=Tomato&minPrice=1&maxPrice=10&categoryId=660e8400-e29b-41d4-a716-446655440001&page=0&size=5" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

---

## 4. Error Cases Testing

### 4.1 Duplicate Category Name
```bash
curl -X POST "http://localhost:8080/v1.0/category/" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Vegetables",
    "description": "Duplicate category name test"
  }'
```

### 4.2 Invalid Category ID for Ingredient
```bash
curl -X POST "http://localhost:8080/v1.0/ingredients/" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Ingredient",
    "description": "Test with invalid category",
    "price": 5.50,
    "categoryId": "00000000-0000-0000-0000-000000000000"
  }'
```

### 4.3 Non-existent Category
```bash
curl -X GET "http://localhost:8080/v1.0/category/00000000-0000-0000-0000-000000000000" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 4.4 Invalid Price
```bash
curl -X POST "http://localhost:8080/v1.0/ingredients/" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Ingredient",
    "description": "Test with invalid price",
    "price": -5.50,
    "categoryId": "660e8400-e29b-41d4-a716-446655440001"
  }'
```

### 4.5 Missing Required Fields
```bash
curl -X POST "http://localhost:8080/v1.0/category/" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": ""
  }'
```

---

## 5. Default Data IDs

Use these IDs for testing with existing data:

### Categories:
- Vegetables: `660e8400-e29b-41d4-a716-446655440001`
- Meat: `660e8400-e29b-41d4-a716-446655440002`
- Seafood: `660e8400-e29b-41d4-a716-446655440003`
- Dairy: `660e8400-e29b-41d4-a716-446655440004`
- Grains: `660e8400-e29b-41d4-a716-446655440005`
- Spices: `660e8400-e29b-41d4-a716-446655440006`
- Fruits: `660e8400-e29b-41d4-a716-446655440007`
- Beverages: `660e8400-e29b-41d4-a716-446655440008`

### Sample Ingredients:
- Tomato: `880e8400-e29b-41d4-a716-446655440001`
- Chicken Breast: `880e8400-e29b-41d4-a716-446655440006`
- Salmon Fillet: `880e8400-e29b-41d4-a716-446655440010`

---

## 6. Quick Test Sequence

1. **Login** and copy the JWT token
2. **Get all categories** to see existing data
3. **Create a new category** and note the returned ID
4. **Get the created category** by ID
5. **Update the category** with new data
6. **Create an ingredient** in the new category
7. **Get all ingredients** to see the new ingredient
8. **Filter ingredients** by various criteria
9. **Delete the ingredient** and category
10. **Test error cases** to verify validation

---

## Notes

- Replace `YOUR_JWT_TOKEN` with the actual token from the login response
- Replace `CATEGORY_ID` and `INGREDIENT_ID` with actual IDs from responses
- All timestamps are in ISO format
- Price values should be positive numbers
- Category names must be unique within the tenant
- All operations are scoped to the default tenant
