package com.conal.dishbuilder.repository;

import com.conal.dishbuilder.domain.CategoryEntity;
import com.conal.dishbuilder.util.TestDataBuilder;
import com.conal.dishbuilder.util.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.config.import-check.enabled=false"
})
class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    private UUID tenantId;
    private CategoryEntity categoryEntity;

    @BeforeEach
    void setUp() {
        tenantId = TestConstants.TEST_TENANT_ID;
        categoryEntity = TestDataBuilder.CategoryBuilder.builder()
                .name("Test Category")
                .description("Test Description")
                .tenantId(tenantId)
                .buildEntity();
    }

    @Test
    void save_Success() {
        // When
        CategoryEntity saved = categoryRepository.save(categoryEntity);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Test Category", saved.getName());
        assertEquals("Test Description", saved.getDescription());
        assertEquals(tenantId, saved.getTenantId());
    }

    @Test
    void findById_Success() {
        // Given
        CategoryEntity saved = entityManager.persistAndFlush(categoryEntity);

        // When
        Optional<CategoryEntity> found = categoryRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("Test Category", found.get().getName());
        assertEquals("Test Description", found.get().getDescription());
        assertEquals(tenantId, found.get().getTenantId());
    }

    @Test
    void findById_NotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        Optional<CategoryEntity> found = categoryRepository.findById(nonExistentId);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void existsByNameAndTenantId_Exists() {
        // Given
        entityManager.persistAndFlush(categoryEntity);

        // When
        boolean exists = categoryRepository.existsByNameAndTenantId("Test Category", tenantId);

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByNameAndTenantId_NotExists() {
        // Given
        entityManager.persistAndFlush(categoryEntity);

        // When
        boolean exists = categoryRepository.existsByNameAndTenantId("Non Existent Category", tenantId);

        // Then
        assertFalse(exists);
    }

    @Test
    void existsByNameAndTenantId_DifferentTenant() {
        // Given
        entityManager.persistAndFlush(categoryEntity);
        UUID differentTenantId = UUID.randomUUID();

        // When
        boolean exists = categoryRepository.existsByNameAndTenantId("Test Category", differentTenantId);

        // Then
        assertFalse(exists);
    }

    @Test
    void delete_Success() {
        // Given
        CategoryEntity saved = entityManager.persistAndFlush(categoryEntity);

        // When
        categoryRepository.delete(saved);

        // Then
        Optional<CategoryEntity> found = categoryRepository.findById(saved.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void findAll_Success() {
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

        entityManager.persistAndFlush(category1);
        entityManager.persistAndFlush(category2);

        // When
        var allCategories = categoryRepository.findAll();

        // Then
        assertTrue(allCategories.size() >= 2);
        assertTrue(allCategories.stream().anyMatch(c -> c.getName().equals("Category 1")));
        assertTrue(allCategories.stream().anyMatch(c -> c.getName().equals("Category 2")));
    }

    @Test
    void save_UpdateExisting() {
        // Given
        CategoryEntity saved = entityManager.persistAndFlush(categoryEntity);
        saved.setName("Updated Category");
        saved.setDescription("Updated Description");

        // When
        CategoryEntity updated = categoryRepository.save(saved);

        // Then
        assertEquals(saved.getId(), updated.getId());
        assertEquals("Updated Category", updated.getName());
        assertEquals("Updated Description", updated.getDescription());
        assertEquals(tenantId, updated.getTenantId());
    }
}
