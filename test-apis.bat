@echo off
REM Test APIs for Category and Ingredients on Windows
REM Base URL
set BASE_URL=http://localhost:8080/v1.0

echo === DISH BUILDER API TESTING ===
echo.

REM 1. Login to get JWT token
echo === 1. LOGIN TO GET JWT TOKEN ===
echo Logging in with superadmin...
curl -s -X POST "%BASE_URL%/auth/login" ^
  -H "Content-Type: application/json" ^
  -d "{\"username\": \"superadmin\", \"password\": \"admin123\"}"

echo.
echo.

REM Note: For Windows batch file, we'll use a simpler approach
REM You'll need to manually copy the token from the response above

echo Please copy the JWT token from the response above and set it in the TOKEN variable below
echo Then uncomment the test sections you want to run
echo.

REM Set your token here after getting it from the login response
REM set TOKEN=your_jwt_token_here
REM set AUTH_HEADER=Authorization: Bearer %TOKEN%

echo === 2. CATEGORY APIs TESTING ===
echo.

echo 2.1 Getting all categories...
echo curl -s -X GET "%BASE_URL%/category/" -H "Authorization: Bearer YOUR_TOKEN" -H "Content-Type: application/json"
echo.

echo 2.2 Creating new category...
echo curl -s -X POST "%BASE_URL%/category/" -H "Authorization: Bearer YOUR_TOKEN" -H "Content-Type: application/json" -d "{\"name\": \"Test Category\", \"description\": \"This is a test category created via API\"}"
echo.

echo 2.3 Getting category by ID (replace CATEGORY_ID with actual ID)...
echo curl -s -X GET "%BASE_URL%/category/CATEGORY_ID" -H "Authorization: Bearer YOUR_TOKEN" -H "Content-Type: application/json"
echo.

echo 2.4 Updating category (replace CATEGORY_ID with actual ID)...
echo curl -s -X PUT "%BASE_URL%/category/CATEGORY_ID" -H "Authorization: Bearer YOUR_TOKEN" -H "Content-Type: application/json" -d "{\"name\": \"Updated Test Category\", \"description\": \"This category has been updated via API\"}"
echo.

echo 2.5 Deleting category (replace CATEGORY_ID with actual ID)...
echo curl -s -X DELETE "%BASE_URL%/category/CATEGORY_ID" -H "Authorization: Bearer YOUR_TOKEN" -H "Content-Type: application/json"
echo.

echo 2.6 Testing category filtering...
echo curl -s -X GET "%BASE_URL%/category/?name=Vegetables&page=0&size=5" -H "Authorization: Bearer YOUR_TOKEN" -H "Content-Type: application/json"
echo.

echo === 3. INGREDIENTS APIs TESTING ===
echo.

echo 3.1 Getting all ingredients...
echo curl -s -X GET "%BASE_URL%/ingredients/" -H "Authorization: Bearer YOUR_TOKEN" -H "Content-Type: application/json"
echo.

echo 3.2 Creating new ingredient...
echo curl -s -X POST "%BASE_URL%/ingredients/" -H "Authorization: Bearer YOUR_TOKEN" -H "Content-Type: application/json" -d "{\"name\": \"Test Ingredient\", \"description\": \"This is a test ingredient created via API\", \"price\": 5.50, \"categoryId\": \"660e8400-e29b-41d4-a716-446655440001\"}"
echo.

echo 3.3 Getting ingredient by ID (replace INGREDIENT_ID with actual ID)...
echo curl -s -X GET "%BASE_URL%/ingredients/INGREDIENT_ID" -H "Authorization: Bearer YOUR_TOKEN" -H "Content-Type: application/json"
echo.

echo 3.4 Updating ingredient (replace INGREDIENT_ID with actual ID)...
echo curl -s -X PUT "%BASE_URL%/ingredients/INGREDIENT_ID" -H "Authorization: Bearer YOUR_TOKEN" -H "Content-Type: application/json" -d "{\"name\": \"Updated Test Ingredient\", \"description\": \"This ingredient has been updated via API\", \"price\": 7.25}"
echo.

echo 3.5 Deleting ingredient (replace INGREDIENT_ID with actual ID)...
echo curl -s -X DELETE "%BASE_URL%/ingredients/INGREDIENT_ID" -H "Authorization: Bearer YOUR_TOKEN" -H "Content-Type: application/json"
echo.

echo 3.6 Testing ingredient filtering...
echo curl -s -X GET "%BASE_URL%/ingredients/?name=Tomato&minPrice=1&maxPrice=10&page=0&size=5" -H "Authorization: Bearer YOUR_TOKEN" -H "Content-Type: application/json"
echo.

echo 3.7 Testing ingredient filtering by category...
echo curl -s -X GET "%BASE_URL%/ingredients/?categoryId=660e8400-e29b-41d4-a716-446655440001&page=0&size=10" -H "Authorization: Bearer YOUR_TOKEN" -H "Content-Type: application/json"
echo.

echo === 4. ERROR CASES TESTING ===
echo.

echo 4.1 Testing duplicate category name...
echo curl -s -X POST "%BASE_URL%/category/" -H "Authorization: Bearer YOUR_TOKEN" -H "Content-Type: application/json" -d "{\"name\": \"Vegetables\", \"description\": \"Duplicate category name test\"}"
echo.

echo 4.2 Testing invalid category ID...
echo curl -s -X POST "%BASE_URL%/ingredients/" -H "Authorization: Bearer YOUR_TOKEN" -H "Content-Type: application/json" -d "{\"name\": \"Test Ingredient\", \"description\": \"Test with invalid category\", \"price\": 5.50, \"categoryId\": \"00000000-0000-0000-0000-000000000000\"}"
echo.

echo 4.3 Testing non-existent category...
echo curl -s -X GET "%BASE_URL%/category/00000000-0000-0000-0000-000000000000" -H "Authorization: Bearer YOUR_TOKEN" -H "Content-Type: application/json"
echo.

echo === TESTING COMPLETED ===
echo All API test commands have been displayed!
echo Copy and paste the commands above, replacing YOUR_TOKEN with the actual JWT token.
echo.
pause
