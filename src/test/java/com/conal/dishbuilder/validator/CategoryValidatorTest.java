package com.conal.dishbuilder.validator;

import com.conal.dishbuilder.context.TenantContextHolder;
import com.conal.dishbuilder.domain.CategoryEntity;
import com.conal.dishbuilder.dto.CreateCategoryRequest;
import com.conal.dishbuilder.dto.UpdateCategoryRequest;
import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import com.conal.dishbuilder.repository.CategoryRepository;
import com.conal.dishbuilder.util.TestConstants;
import com.conal.dishbuilder.util.TestDataBuilder;
import com.conal.dishbuilder.validator.CategoryValidatorImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryValidatorTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Validator validator;

    @InjectMocks
    private CategoryValidatorImpl categoryValidator;

    private UUID tenantId;
    private CreateCategoryRequest createRequest;
    private UpdateCategoryRequest updateRequest;
    private CategoryEntity categoryEntity;

    @BeforeEach
    void setUp() {
        tenantId = TestConstants.TEST_TENANT_ID;
        
        createRequest = TestDataBuilder.CategoryBuilder.builder()
                .name(TestConstants.TEST_CATEGORY_NAME)
                .description(TestConstants.TEST_CATEGORY_DESCRIPTION)
                .buildCreateRequest();
                
        updateRequest = TestDataBuilder.CategoryBuilder.builder()
                .name("Updated Category")
                .description("Updated Description")
                .buildUpdateRequest();
                
        categoryEntity = TestDataBuilder.CategoryBuilder.builder()
                .id(TestConstants.TEST_CATEGORY_ID)
                .name(TestConstants.TEST_CATEGORY_NAME)
                .description(TestConstants.TEST_CATEGORY_DESCRIPTION)
                .tenantId(tenantId)
                .buildEntity();
    }

    @Test
    void validateCreateCategory_Success() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(validator.validate(createRequest)).thenReturn(Collections.emptySet());
            when(categoryRepository.existsByNameAndTenantId(createRequest.getName().trim(), tenantId))
                    .thenReturn(false);

            // When
            List<FieldErrorResponse> result = categoryValidator.validateCreateCategory(createRequest);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(validator).validate(createRequest);
            verify(categoryRepository).existsByNameAndTenantId(createRequest.getName().trim(), tenantId);
        }
    }

    @Test
    void validateCreateCategory_NameAlreadyExists() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(validator.validate(createRequest)).thenReturn(Collections.emptySet());
            when(categoryRepository.existsByNameAndTenantId(createRequest.getName().trim(), tenantId))
                    .thenReturn(true);

            // When
            List<FieldErrorResponse> result = categoryValidator.validateCreateCategory(createRequest);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("name", result.get(0).getField());
            assertEquals(createRequest.getName(), result.get(0).getRejectedValue());
            assertEquals("Name already exists.", result.get(0).getMessage());

            verify(validator).validate(createRequest);
            verify(categoryRepository).existsByNameAndTenantId(createRequest.getName().trim(), tenantId);
        }
    }

    @Test
    void validateCreateCategory_ValidationErrors() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            @SuppressWarnings("unchecked")
            ConstraintViolation<CreateCategoryRequest> violation = mock(ConstraintViolation.class);
            Path propertyPath = mock(Path.class);
            when(propertyPath.toString()).thenReturn("name");
            when(violation.getPropertyPath()).thenReturn(propertyPath);
            when(violation.getInvalidValue()).thenReturn("");
            when(violation.getMessage()).thenReturn("Name is required");
            
            when(validator.validate(createRequest)).thenReturn(Collections.singleton(violation));
            when(categoryRepository.existsByNameAndTenantId(createRequest.getName().trim(), tenantId))
                    .thenReturn(false);

            // When
            List<FieldErrorResponse> result = categoryValidator.validateCreateCategory(createRequest);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("name", result.get(0).getField());
            assertEquals("", result.get(0).getRejectedValue());
            assertEquals("Name is required", result.get(0).getMessage());

            verify(validator).validate(createRequest);
            verify(categoryRepository).existsByNameAndTenantId(createRequest.getName().trim(), tenantId);
        }
    }

    @Test
    void validateUpdateCategory_Success() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(validator.validate(updateRequest)).thenReturn(Collections.emptySet());
            when(categoryRepository.existsByNameAndTenantId(updateRequest.getName().trim(), tenantId))
                    .thenReturn(false);

            // When
            List<FieldErrorResponse> result = categoryValidator.validateUpdateCategory(updateRequest, categoryEntity);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(validator).validate(updateRequest);
            verify(categoryRepository).existsByNameAndTenantId(updateRequest.getName().trim(), tenantId);
        }
    }

    @Test
    void validateUpdateCategory_NameAlreadyExists() {
        // Given
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(validator.validate(updateRequest)).thenReturn(Collections.emptySet());
            when(categoryRepository.existsByNameAndTenantId(updateRequest.getName().trim(), tenantId))
                    .thenReturn(true);

            // When
            List<FieldErrorResponse> result = categoryValidator.validateUpdateCategory(updateRequest, categoryEntity);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("name", result.get(0).getField());
            assertEquals(updateRequest.getName(), result.get(0).getRejectedValue());
            assertEquals("Name already exists.", result.get(0).getMessage());

            verify(validator).validate(updateRequest);
            verify(categoryRepository).existsByNameAndTenantId(updateRequest.getName().trim(), tenantId);
        }
    }

    @Test
    void validateUpdateCategory_SameName() {
        // Given
        updateRequest.setName(TestConstants.TEST_CATEGORY_NAME); // Same as existing
        
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(validator.validate(updateRequest)).thenReturn(Collections.emptySet());

            // When
            List<FieldErrorResponse> result = categoryValidator.validateUpdateCategory(updateRequest, categoryEntity);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(validator).validate(updateRequest);
            verify(categoryRepository, never()).existsByNameAndTenantId(any(), any());
        }
    }

    @Test
    void validateUpdateCategory_NameLengthValidation() {
        // Given
        updateRequest.setName(""); // Empty name
        
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(validator.validate(updateRequest)).thenReturn(Collections.emptySet());

            // When
            List<FieldErrorResponse> result = categoryValidator.validateUpdateCategory(updateRequest, categoryEntity);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("name", result.get(0).getField());
            assertEquals("", result.get(0).getRejectedValue());
            assertEquals("Name must be in range 1 to 100 character", result.get(0).getMessage());

            verify(validator).validate(updateRequest);
        }
    }

    @Test
    void validateUpdateCategory_DescriptionLengthValidation() {
        // Given
        updateRequest.setDescription(""); // Empty description
        
        try (MockedStatic<TenantContextHolder> mockedStatic = mockStatic(TenantContextHolder.class)) {
            mockedStatic.when(TenantContextHolder::getTenantContext).thenReturn(tenantId);
            
            when(validator.validate(updateRequest)).thenReturn(Collections.emptySet());

            // When
            List<FieldErrorResponse> result = categoryValidator.validateUpdateCategory(updateRequest, categoryEntity);

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("description", result.get(0).getField());
            assertEquals("", result.get(0).getRejectedValue());
            assertEquals("Description must be in range 1 to 255 character", result.get(0).getMessage());

            verify(validator).validate(updateRequest);
        }
    }
}
