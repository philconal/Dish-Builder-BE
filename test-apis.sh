#!/bin/bash

# Test APIs for Category and Ingredients
# Base URL
BASE_URL="http://localhost:8080/v1.0"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== DISH BUILDER API TESTING ===${NC}"
echo ""

# Function to print section headers
print_section() {
    echo -e "${YELLOW}=== $1 ===${NC}"
    echo ""
}

# Function to print test result
print_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓ $2${NC}"
    else
        echo -e "${RED}✗ $2${NC}"
    fi
    echo ""
}

# 1. Login to get JWT token
print_section "1. LOGIN TO GET JWT TOKEN"
echo "Logging in with superadmin..."
TOKEN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "superadmin",
    "password": "admin123"
  }')

echo "Response: $TOKEN_RESPONSE"
echo ""

# Extract token (assuming response format: {"data": {"token": "..."}})
TOKEN=$(echo $TOKEN_RESPONSE | grep -o '"accessToken":"[^"]*' | grep -o '[^"]*$')

if [ -z "$TOKEN" ]; then
    echo -e "${RED}Failed to get JWT token. Please check if the server is running and credentials are correct.${NC}"
    exit 1
fi

echo -e "${GREEN}JWT Token obtained: ${TOKEN:0:50}...${NC}"
echo ""

# Set Authorization header
AUTH_HEADER="Authorization: Bearer $TOKEN"

# 2. Test Category APIs
print_section "2. CATEGORY APIs TESTING"

# 2.1 Get all categories
echo "2.1 Getting all categories..."
CATEGORIES_RESPONSE=$(curl -s -X GET "$BASE_URL/category/" \
  -H "$AUTH_HEADER" \
  -H "Content-Type: application/json")
print_result $? "Get all categories"
echo "Response: $CATEGORIES_RESPONSE"
echo ""

# 2.2 Create new category
echo "2.2 Creating new category..."
CREATE_CATEGORY_RESPONSE=$(curl -s -X POST "$BASE_URL/category/" \
  -H "$AUTH_HEADER" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Category",
    "description": "This is a test category created via API"
  }')
print_result $? "Create new category"
echo "Response: $CREATE_CATEGORY_RESPONSE"
echo ""

# Extract category ID from response (assuming it returns the created category with ID)
CATEGORY_ID=$(echo $CREATE_CATEGORY_RESPONSE | grep -o '"id":"[^"]*' | grep -o '[^"]*$' | head -1)

if [ -n "$CATEGORY_ID" ]; then
    echo -e "${GREEN}Created category ID: $CATEGORY_ID${NC}"
    
    # 2.3 Get category by ID
    echo "2.3 Getting category by ID..."
    GET_CATEGORY_RESPONSE=$(curl -s -X GET "$BASE_URL/category/$CATEGORY_ID" \
      -H "$AUTH_HEADER" \
      -H "Content-Type: application/json")
    print_result $? "Get category by ID"
    echo "Response: $GET_CATEGORY_RESPONSE"
    echo ""
    
    # 2.4 Update category
    echo "2.4 Updating category..."
    UPDATE_CATEGORY_RESPONSE=$(curl -s -X PUT "$BASE_URL/category/$CATEGORY_ID" \
      -H "$AUTH_HEADER" \
      -H "Content-Type: application/json" \
      -d '{
        "name": "Updated Test Category",
        "description": "This category has been updated via API"
      }')
    print_result $? "Update category"
    echo "Response: $UPDATE_CATEGORY_RESPONSE"
    echo ""
    
    # 2.5 Delete category
    echo "2.5 Deleting category..."
    DELETE_CATEGORY_RESPONSE=$(curl -s -X DELETE "$BASE_URL/category/$CATEGORY_ID" \
      -H "$AUTH_HEADER" \
      -H "Content-Type: application/json")
    print_result $? "Delete category"
    echo "Response: $DELETE_CATEGORY_RESPONSE"
    echo ""
else
    echo -e "${YELLOW}Could not extract category ID, skipping individual category tests${NC}"
fi

# 2.6 Test category filtering
echo "2.6 Testing category filtering..."
FILTER_CATEGORIES_RESPONSE=$(curl -s -X GET "$BASE_URL/category/?name=Vegetables&page=0&size=5" \
  -H "$AUTH_HEADER" \
  -H "Content-Type: application/json")
print_result $? "Filter categories by name"
echo "Response: $FILTER_CATEGORIES_RESPONSE"
echo ""

# 3. Test Ingredients APIs
print_section "3. INGREDIENTS APIs TESTING"

# 3.1 Get all ingredients
echo "3.1 Getting all ingredients..."
INGREDIENTS_RESPONSE=$(curl -s -X GET "$BASE_URL/ingredients/" \
  -H "$AUTH_HEADER" \
  -H "Content-Type: application/json")
print_result $? "Get all ingredients"
echo "Response: $INGREDIENTS_RESPONSE"
echo ""

# Get a category ID for creating ingredients (use the first default category)
DEFAULT_CATEGORY_ID="660e8400-e29b-41d4-a716-446655440001" # Vegetables category

# 3.2 Create new ingredient
echo "3.2 Creating new ingredient..."
CREATE_INGREDIENT_RESPONSE=$(curl -s -X POST "$BASE_URL/ingredients/" \
  -H "$AUTH_HEADER" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Test Ingredient\",
    \"description\": \"This is a test ingredient created via API\",
    \"price\": 5.50,
    \"categoryId\": \"$DEFAULT_CATEGORY_ID\"
  }")
print_result $? "Create new ingredient"
echo "Response: $CREATE_INGREDIENT_RESPONSE"
echo ""

# Extract ingredient ID from response
INGREDIENT_ID=$(echo $CREATE_INGREDIENT_RESPONSE | grep -o '"id":"[^"]*' | grep -o '[^"]*$' | head -1)

if [ -n "$INGREDIENT_ID" ]; then
    echo -e "${GREEN}Created ingredient ID: $INGREDIENT_ID${NC}"
    
    # 3.3 Get ingredient by ID
    echo "3.3 Getting ingredient by ID..."
    GET_INGREDIENT_RESPONSE=$(curl -s -X GET "$BASE_URL/ingredients/$INGREDIENT_ID" \
      -H "$AUTH_HEADER" \
      -H "Content-Type: application/json")
    print_result $? "Get ingredient by ID"
    echo "Response: $GET_INGREDIENT_RESPONSE"
    echo ""
    
    # 3.4 Update ingredient
    echo "3.4 Updating ingredient..."
    UPDATE_INGREDIENT_RESPONSE=$(curl -s -X PUT "$BASE_URL/ingredients/$INGREDIENT_ID" \
      -H "$AUTH_HEADER" \
      -H "Content-Type: application/json" \
      -d "{
        \"name\": \"Updated Test Ingredient\",
        \"description\": \"This ingredient has been updated via API\",
        \"price\": 7.25
      }")
    print_result $? "Update ingredient"
    echo "Response: $UPDATE_INGREDIENT_RESPONSE"
    echo ""
    
    # 3.5 Delete ingredient
    echo "3.5 Deleting ingredient..."
    DELETE_INGREDIENT_RESPONSE=$(curl -s -X DELETE "$BASE_URL/ingredients/$INGREDIENT_ID" \
      -H "$AUTH_HEADER" \
      -H "Content-Type: application/json")
    print_result $? "Delete ingredient"
    echo "Response: $DELETE_INGREDIENT_RESPONSE"
    echo ""
else
    echo -e "${YELLOW}Could not extract ingredient ID, skipping individual ingredient tests${NC}"
fi

# 3.6 Test ingredient filtering
echo "3.6 Testing ingredient filtering..."
FILTER_INGREDIENTS_RESPONSE=$(curl -s -X GET "$BASE_URL/ingredients/?name=Tomato&minPrice=1&maxPrice=10&page=0&size=5" \
  -H "$AUTH_HEADER" \
  -H "Content-Type: application/json")
print_result $? "Filter ingredients by name and price range"
echo "Response: $FILTER_INGREDIENTS_RESPONSE"
echo ""

# 3.7 Test ingredient filtering by category
echo "3.7 Testing ingredient filtering by category..."
FILTER_BY_CATEGORY_RESPONSE=$(curl -s -X GET "$BASE_URL/ingredients/?categoryId=$DEFAULT_CATEGORY_ID&page=0&size=10" \
  -H "$AUTH_HEADER" \
  -H "Content-Type: application/json")
print_result $? "Filter ingredients by category"
echo "Response: $FILTER_BY_CATEGORY_RESPONSE"
echo ""

# 4. Test error cases
print_section "4. ERROR CASES TESTING"

# 4.1 Test creating category with duplicate name
echo "4.1 Testing duplicate category name..."
DUPLICATE_CATEGORY_RESPONSE=$(curl -s -X POST "$BASE_URL/category/" \
  -H "$AUTH_HEADER" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Vegetables",
    "description": "Duplicate category name test"
  }')
print_result $? "Create category with duplicate name (should fail)"
echo "Response: $DUPLICATE_CATEGORY_RESPONSE"
echo ""

# 4.2 Test creating ingredient with invalid category
echo "4.2 Testing invalid category ID..."
INVALID_CATEGORY_RESPONSE=$(curl -s -X POST "$BASE_URL/ingredients/" \
  -H "$AUTH_HEADER" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Ingredient",
    "description": "Test with invalid category",
    "price": 5.50,
    "categoryId": "00000000-0000-0000-0000-000000000000"
  }')
print_result $? "Create ingredient with invalid category (should fail)"
echo "Response: $INVALID_CATEGORY_RESPONSE"
echo ""

# 4.3 Test getting non-existent category
echo "4.3 Testing non-existent category..."
NOT_FOUND_CATEGORY_RESPONSE=$(curl -s -X GET "$BASE_URL/category/00000000-0000-0000-0000-000000000000" \
  -H "$AUTH_HEADER" \
  -H "Content-Type: application/json")
print_result $? "Get non-existent category (should fail)"
echo "Response: $NOT_FOUND_CATEGORY_RESPONSE"
echo ""

print_section "TESTING COMPLETED"
echo -e "${GREEN}All API tests have been executed!${NC}"
echo "Check the responses above for any errors or unexpected results."
echo ""
echo -e "${BLUE}Note: Some tests may fail if the server is not running or if there are validation errors.${NC}"
echo -e "${BLUE}This is normal behavior for error case testing.${NC}"
