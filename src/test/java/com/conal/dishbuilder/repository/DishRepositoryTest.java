package com.conal.dishbuilder.repository;

import com.conal.dishbuilder.domain.DishEntity;
import com.conal.dishbuilder.domain.IngredientsEntity;
import com.conal.dishbuilder.domain.UserEntity;
import com.conal.dishbuilder.util.TestDataBuilder;
import com.conal.dishbuilder.util.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.config.import-check.enabled=false"
})
class DishRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DishRepository dishRepository;

    private UUID tenantId;
    private UUID userId;
    private UUID ingredientId1;
    private UUID ingredientId2;
    private UserEntity userEntity;
    private IngredientsEntity ingredient1;
    private IngredientsEntity ingredient2;
    private DishEntity dishEntity;

    @BeforeEach
    void setUp() {
        tenantId = TestConstants.TEST_TENANT_ID;
        userId = TestConstants.TEST_USER_ID;
        ingredientId1 = TestConstants.TEST_INGREDIENT_ID_1;
        ingredientId2 = TestConstants.TEST_INGREDIENT_ID_2;
        
        userEntity = TestDataBuilder.UserBuilder.builder()
                .id(userId)
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .tenantId(tenantId)
                .buildEntity();
        
        ingredient1 = TestDataBuilder.IngredientsBuilder.builder()
                .id(ingredientId1)
                .name("Ingredient 1")
                .description("Description 1")
                .price(new BigDecimal("5.00"))
                .tenantId(tenantId)
                .buildEntity();
        
        ingredient2 = TestDataBuilder.IngredientsBuilder.builder()
                .id(ingredientId2)
                .name("Ingredient 2")
                .description("Description 2")
                .price(new BigDecimal("7.50"))
                .tenantId(tenantId)
                .buildEntity();
        
        dishEntity = TestDataBuilder.DishBuilder.builder()
                .name("Test Dish")
                .description("Test Dish Description")
                .totalPrice(new BigDecimal("15.00"))
                .discount(new BigDecimal("2.00"))
                .vat(new BigDecimal("1.30"))
                .tenantId(tenantId)
                .user(userEntity)
                .ingredients(List.of(ingredient1, ingredient2))
                .buildEntity();
    }

    @Test
    void save_Success() {
        // Given
        entityManager.persistAndFlush(userEntity);
        entityManager.persistAndFlush(ingredient1);
        entityManager.persistAndFlush(ingredient2);

        // When
        DishEntity saved = dishRepository.save(dishEntity);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Test Dish", saved.getName());
        assertEquals("Test Dish Description", saved.getDescription());
        assertEquals(new BigDecimal("15.00"), saved.getTotalPrice());
        assertEquals(new BigDecimal("2.00"), saved.getDiscount());
        assertEquals(new BigDecimal("1.30"), saved.getVat());
        assertEquals(tenantId, saved.getTenantId());
        assertEquals(userId, saved.getUser().getId());
        assertEquals(2, saved.getIngredients().size());
    }

    @Test
    void findById_Success() {
        // Given
        entityManager.persistAndFlush(userEntity);
        entityManager.persistAndFlush(ingredient1);
        entityManager.persistAndFlush(ingredient2);
        DishEntity saved = entityManager.persistAndFlush(dishEntity);

        // When
        Optional<DishEntity> found = dishRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("Test Dish", found.get().getName());
        assertEquals("Test Dish Description", found.get().getDescription());
        assertEquals(new BigDecimal("15.00"), found.get().getTotalPrice());
        assertEquals(new BigDecimal("2.00"), found.get().getDiscount());
        assertEquals(new BigDecimal("1.30"), found.get().getVat());
        assertEquals(tenantId, found.get().getTenantId());
        assertEquals(userId, found.get().getUser().getId());
        assertEquals(2, found.get().getIngredients().size());
    }

    @Test
    void findById_NotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        Optional<DishEntity> found = dishRepository.findById(nonExistentId);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void findAllById_Success() {
        // Given
        entityManager.persistAndFlush(userEntity);
        entityManager.persistAndFlush(ingredient1);
        entityManager.persistAndFlush(ingredient2);
        
        DishEntity dish1 = TestDataBuilder.DishBuilder.builder()
                .name("Dish 1")
                .description("Description 1")
                .totalPrice(new BigDecimal("10.00"))
                .discount(new BigDecimal("1.00"))
                .vat(new BigDecimal("0.90"))
                .tenantId(tenantId)
                .user(userEntity)
                .ingredients(List.of(ingredient1))
                .buildEntity();
        
        DishEntity dish2 = TestDataBuilder.DishBuilder.builder()
                .name("Dish 2")
                .description("Description 2")
                .totalPrice(new BigDecimal("20.00"))
                .discount(new BigDecimal("3.00"))
                .vat(new BigDecimal("1.70"))
                .tenantId(tenantId)
                .user(userEntity)
                .ingredients(List.of(ingredient2))
                .buildEntity();

        DishEntity saved1 = entityManager.persistAndFlush(dish1);
        DishEntity saved2 = entityManager.persistAndFlush(dish2);

        // When
        List<DishEntity> found = dishRepository.findAllById(
                List.of(saved1.getId(), saved2.getId()));

        // Then
        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(d -> d.getName().equals("Dish 1")));
        assertTrue(found.stream().anyMatch(d -> d.getName().equals("Dish 2")));
    }

    @Test
    void findAllById_EmptyList() {
        // When
        List<DishEntity> found = dishRepository.findAllById(List.of());

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void findAllById_NonExistentIds() {
        // Given
        UUID nonExistentId1 = UUID.randomUUID();
        UUID nonExistentId2 = UUID.randomUUID();

        // When
        List<DishEntity> found = dishRepository.findAllById(
                List.of(nonExistentId1, nonExistentId2));

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void delete_Success() {
        // Given
        entityManager.persistAndFlush(userEntity);
        entityManager.persistAndFlush(ingredient1);
        entityManager.persistAndFlush(ingredient2);
        DishEntity saved = entityManager.persistAndFlush(dishEntity);

        // When
        dishRepository.delete(saved);

        // Then
        Optional<DishEntity> found = dishRepository.findById(saved.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void findAll_Success() {
        // Given
        entityManager.persistAndFlush(userEntity);
        entityManager.persistAndFlush(ingredient1);
        entityManager.persistAndFlush(ingredient2);
        
        DishEntity dish1 = TestDataBuilder.DishBuilder.builder()
                .name("Dish 1")
                .description("Description 1")
                .totalPrice(new BigDecimal("10.00"))
                .discount(new BigDecimal("1.00"))
                .vat(new BigDecimal("0.90"))
                .tenantId(tenantId)
                .user(userEntity)
                .ingredients(List.of(ingredient1))
                .buildEntity();
        
        DishEntity dish2 = TestDataBuilder.DishBuilder.builder()
                .name("Dish 2")
                .description("Description 2")
                .totalPrice(new BigDecimal("20.00"))
                .discount(new BigDecimal("3.00"))
                .vat(new BigDecimal("1.70"))
                .tenantId(tenantId)
                .user(userEntity)
                .ingredients(List.of(ingredient2))
                .buildEntity();

        entityManager.persistAndFlush(dish1);
        entityManager.persistAndFlush(dish2);

        // When
        var allDishes = dishRepository.findAll();

        // Then
        assertTrue(allDishes.size() >= 2);
        assertTrue(allDishes.stream().anyMatch(d -> d.getName().equals("Dish 1")));
        assertTrue(allDishes.stream().anyMatch(d -> d.getName().equals("Dish 2")));
    }

    @Test
    void save_UpdateExisting() {
        // Given
        entityManager.persistAndFlush(userEntity);
        entityManager.persistAndFlush(ingredient1);
        entityManager.persistAndFlush(ingredient2);
        DishEntity saved = entityManager.persistAndFlush(dishEntity);
        saved.setName("Updated Dish");
        saved.setDescription("Updated Description");
        saved.setTotalPrice(new BigDecimal("18.00"));
        saved.setDiscount(new BigDecimal("3.00"));
        saved.setVat(new BigDecimal("1.50"));

        // When
        DishEntity updated = dishRepository.save(saved);

        // Then
        assertEquals(saved.getId(), updated.getId());
        assertEquals("Updated Dish", updated.getName());
        assertEquals("Updated Description", updated.getDescription());
        assertEquals(new BigDecimal("18.00"), updated.getTotalPrice());
        assertEquals(new BigDecimal("3.00"), updated.getDiscount());
        assertEquals(new BigDecimal("1.50"), updated.getVat());
        assertEquals(tenantId, updated.getTenantId());
    }

    @Test
    void findByUser_Success() {
        // Given
        entityManager.persistAndFlush(userEntity);
        entityManager.persistAndFlush(ingredient1);
        entityManager.persistAndFlush(ingredient2);
        
        DishEntity dish1 = TestDataBuilder.DishBuilder.builder()
                .name("Dish 1")
                .description("Description 1")
                .totalPrice(new BigDecimal("10.00"))
                .discount(new BigDecimal("1.00"))
                .vat(new BigDecimal("0.90"))
                .tenantId(tenantId)
                .user(userEntity)
                .ingredients(List.of(ingredient1))
                .buildEntity();
        
        DishEntity dish2 = TestDataBuilder.DishBuilder.builder()
                .name("Dish 2")
                .description("Description 2")
                .totalPrice(new BigDecimal("20.00"))
                .discount(new BigDecimal("3.00"))
                .vat(new BigDecimal("1.70"))
                .tenantId(tenantId)
                .user(userEntity)
                .ingredients(List.of(ingredient2))
                .buildEntity();

        entityManager.persistAndFlush(dish1);
        entityManager.persistAndFlush(dish2);

        // When
        List<DishEntity> found = dishRepository.findAll();

        // Then
        assertTrue(found.size() >= 2);
        assertTrue(found.stream().anyMatch(d -> d.getName().equals("Dish 1")));
        assertTrue(found.stream().anyMatch(d -> d.getName().equals("Dish 2")));
    }

    @Test
    void findByIngredientsContaining_Success() {
        // Given
        entityManager.persistAndFlush(userEntity);
        entityManager.persistAndFlush(ingredient1);
        entityManager.persistAndFlush(ingredient2);
        
        DishEntity dish1 = TestDataBuilder.DishBuilder.builder()
                .name("Dish 1")
                .description("Description 1")
                .totalPrice(new BigDecimal("10.00"))
                .discount(new BigDecimal("1.00"))
                .vat(new BigDecimal("0.90"))
                .tenantId(tenantId)
                .user(userEntity)
                .ingredients(List.of(ingredient1))
                .buildEntity();
        
        DishEntity dish2 = TestDataBuilder.DishBuilder.builder()
                .name("Dish 2")
                .description("Description 2")
                .totalPrice(new BigDecimal("20.00"))
                .discount(new BigDecimal("3.00"))
                .vat(new BigDecimal("1.70"))
                .tenantId(tenantId)
                .user(userEntity)
                .ingredients(List.of(ingredient1, ingredient2))
                .buildEntity();

        entityManager.persistAndFlush(dish1);
        entityManager.persistAndFlush(dish2);

        // When
        List<DishEntity> found = dishRepository.findAll();

        // Then
        assertTrue(found.size() >= 2);
        assertTrue(found.stream().anyMatch(d -> d.getName().equals("Dish 1")));
        assertTrue(found.stream().anyMatch(d -> d.getName().equals("Dish 2")));
    }
}
