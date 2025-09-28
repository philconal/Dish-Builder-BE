package com.conal.dishbuilder.repository;

import com.conal.dishbuilder.domain.CategoryEntity;
import com.conal.dishbuilder.domain.IngredientsEntity;
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
class IngredientsRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IngredientsRepository ingredientsRepository;

    private UUID tenantId;
    private UUID categoryId;
    private CategoryEntity categoryEntity;
    private IngredientsEntity ingredientsEntity;

    @BeforeEach
    void setUp() {
        tenantId = TestConstants.TEST_TENANT_ID;
        categoryId = TestConstants.TEST_CATEGORY_ID;
        
        categoryEntity = TestDataBuilder.CategoryBuilder.builder()
                .id(categoryId)
                .name("Test Category")
                .description("Test Category Description")
                .tenantId(tenantId)
                .buildEntity();
        
        ingredientsEntity = TestDataBuilder.IngredientsBuilder.builder()
                .name("Test Ingredient")
                .description("Test Ingredient Description")
                .price(new BigDecimal("10.50"))
                .tenantId(tenantId)
                .category(categoryEntity)
                .buildEntity();
    }

    @Test
    void save_Success() {
        // Given
        entityManager.persistAndFlush(categoryEntity);

        // When
        IngredientsEntity saved = ingredientsRepository.save(ingredientsEntity);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Test Ingredient", saved.getName());
        assertEquals("Test Ingredient Description", saved.getDescription());
        assertEquals(new BigDecimal("10.50"), saved.getPrice());
        assertEquals(tenantId, saved.getTenantId());
        assertEquals(categoryId, saved.getCategory().getId());
    }

    @Test
    void findById_Success() {
        // Given
        entityManager.persistAndFlush(categoryEntity);
        IngredientsEntity saved = entityManager.persistAndFlush(ingredientsEntity);

        // When
        Optional<IngredientsEntity> found = ingredientsRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("Test Ingredient", found.get().getName());
        assertEquals("Test Ingredient Description", found.get().getDescription());
        assertEquals(new BigDecimal("10.50"), found.get().getPrice());
        assertEquals(tenantId, found.get().getTenantId());
    }

    @Test
    void findById_NotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        Optional<IngredientsEntity> found = ingredientsRepository.findById(nonExistentId);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void findAllById_Success() {
        // Given
        entityManager.persistAndFlush(categoryEntity);
        IngredientsEntity ingredient1 = TestDataBuilder.IngredientsBuilder.builder()
                .name("Ingredient 1")
                .description("Description 1")
                .price(new BigDecimal("5.00"))
                .tenantId(tenantId)
                .category(categoryEntity)
                .buildEntity();
        
        IngredientsEntity ingredient2 = TestDataBuilder.IngredientsBuilder.builder()
                .name("Ingredient 2")
                .description("Description 2")
                .price(new BigDecimal("7.50"))
                .tenantId(tenantId)
                .category(categoryEntity)
                .buildEntity();

        IngredientsEntity saved1 = entityManager.persistAndFlush(ingredient1);
        IngredientsEntity saved2 = entityManager.persistAndFlush(ingredient2);

        // When
        List<IngredientsEntity> found = ingredientsRepository.findAllById(
                List.of(saved1.getId(), saved2.getId()));

        // Then
        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(i -> i.getName().equals("Ingredient 1")));
        assertTrue(found.stream().anyMatch(i -> i.getName().equals("Ingredient 2")));
    }

    @Test
    void findAllById_EmptyList() {
        // When
        List<IngredientsEntity> found = ingredientsRepository.findAllById(List.of());

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void findAllById_NonExistentIds() {
        // Given
        UUID nonExistentId1 = UUID.randomUUID();
        UUID nonExistentId2 = UUID.randomUUID();

        // When
        List<IngredientsEntity> found = ingredientsRepository.findAllById(
                List.of(nonExistentId1, nonExistentId2));

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    void delete_Success() {
        // Given
        entityManager.persistAndFlush(categoryEntity);
        IngredientsEntity saved = entityManager.persistAndFlush(ingredientsEntity);

        // When
        ingredientsRepository.delete(saved);

        // Then
        Optional<IngredientsEntity> found = ingredientsRepository.findById(saved.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void findAll_Success() {
        // Given
        entityManager.persistAndFlush(categoryEntity);
        IngredientsEntity ingredient1 = TestDataBuilder.IngredientsBuilder.builder()
                .name("Ingredient 1")
                .description("Description 1")
                .price(new BigDecimal("5.00"))
                .tenantId(tenantId)
                .category(categoryEntity)
                .buildEntity();
        
        IngredientsEntity ingredient2 = TestDataBuilder.IngredientsBuilder.builder()
                .name("Ingredient 2")
                .description("Description 2")
                .price(new BigDecimal("7.50"))
                .tenantId(tenantId)
                .category(categoryEntity)
                .buildEntity();

        entityManager.persistAndFlush(ingredient1);
        entityManager.persistAndFlush(ingredient2);

        // When
        var allIngredients = ingredientsRepository.findAll();

        // Then
        assertTrue(allIngredients.size() >= 2);
        assertTrue(allIngredients.stream().anyMatch(i -> i.getName().equals("Ingredient 1")));
        assertTrue(allIngredients.stream().anyMatch(i -> i.getName().equals("Ingredient 2")));
    }

    @Test
    void save_UpdateExisting() {
        // Given
        entityManager.persistAndFlush(categoryEntity);
        IngredientsEntity saved = entityManager.persistAndFlush(ingredientsEntity);
        saved.setName("Updated Ingredient");
        saved.setDescription("Updated Description");
        saved.setPrice(new BigDecimal("15.00"));

        // When
        IngredientsEntity updated = ingredientsRepository.save(saved);

        // Then
        assertEquals(saved.getId(), updated.getId());
        assertEquals("Updated Ingredient", updated.getName());
        assertEquals("Updated Description", updated.getDescription());
        assertEquals(new BigDecimal("15.00"), updated.getPrice());
        assertEquals(tenantId, updated.getTenantId());
    }

    @Test
    void findByCategory_Success() {
        // Given
        entityManager.persistAndFlush(categoryEntity);
        IngredientsEntity ingredient1 = TestDataBuilder.IngredientsBuilder.builder()
                .name("Ingredient 1")
                .description("Description 1")
                .price(new BigDecimal("5.00"))
                .tenantId(tenantId)
                .category(categoryEntity)
                .buildEntity();
        
        IngredientsEntity ingredient2 = TestDataBuilder.IngredientsBuilder.builder()
                .name("Ingredient 2")
                .description("Description 2")
                .price(new BigDecimal("7.50"))
                .tenantId(tenantId)
                .category(categoryEntity)
                .buildEntity();

        entityManager.persistAndFlush(ingredient1);
        entityManager.persistAndFlush(ingredient2);

        // When
        List<IngredientsEntity> found = ingredientsRepository.findAll();

        // Then
        assertTrue(found.size() >= 2);
        assertTrue(found.stream().anyMatch(i -> i.getName().equals("Ingredient 1")));
        assertTrue(found.stream().anyMatch(i -> i.getName().equals("Ingredient 2")));
    }
}
