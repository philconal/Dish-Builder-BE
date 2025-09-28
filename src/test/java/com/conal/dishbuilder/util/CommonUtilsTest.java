package com.conal.dishbuilder.util;

import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.config.import-check.enabled=false"
})
class CommonUtilsTest {

    @Autowired
    private Validator validator;

    private TestObject testObject;

    @BeforeEach
    void setUp() {
        testObject = new TestObject();
    }

    @Test
    void buildFieldErrorResponse_WithValidViolation() {
        // Given
        testObject.setName("");
        testObject.setDescription(null);
        Set<ConstraintViolation<TestObject>> violations = validator.validate(testObject);
        ConstraintViolation<TestObject> violation = violations.iterator().next();

        // When
        FieldErrorResponse response = CommonUtils.buildFieldErrorResponse(
                violation.getPropertyPath().toString(),
                violation.getInvalidValue() != null ? violation.getInvalidValue().toString() : null,
                violation.getMessage()
        );

        // Then
        assertNotNull(response);
        assertNotNull(response.getField());
        assertNotNull(response.getRejectedValue());
        assertNotNull(response.getMessage());
    }

    @Test
    void buildFieldErrorResponse_WithNullViolation() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            CommonUtils.buildFieldErrorResponse(null);
        });
    }

    @Test
    void buildFieldErrorResponse_WithValidFieldError() {
        // Given
        String field = "testField";
        Object rejectedValue = "testValue";
        String message = "Test error message";

        // When
        FieldErrorResponse response = CommonUtils.buildFieldErrorResponse(field, rejectedValue.toString(), message);

        // Then
        assertNotNull(response);
        assertEquals(field, response.getField());
        assertEquals(rejectedValue, response.getRejectedValue());
        assertEquals(message, response.getMessage());
    }

    @Test
    void buildFieldErrorResponse_WithNullField() {
        // Given
        String field = null;
        Object rejectedValue = "testValue";
        String message = "Test error message";

        // When
        FieldErrorResponse response = CommonUtils.buildFieldErrorResponse(field, rejectedValue.toString(), message);

        // Then
        assertNotNull(response);
        assertNull(response.getField());
        assertEquals(rejectedValue, response.getRejectedValue());
        assertEquals(message, response.getMessage());
    }

    @Test
    void buildFieldErrorResponse_WithNullRejectedValue() {
        // Given
        String field = "testField";
        Object rejectedValue = null;
        String message = "Test error message";

        // When
        FieldErrorResponse response = CommonUtils.buildFieldErrorResponse(field, rejectedValue.toString(), message);

        // Then
        assertNotNull(response);
        assertEquals(field, response.getField());
        assertNull(response.getRejectedValue());
        assertEquals(message, response.getMessage());
    }

    @Test
    void buildFieldErrorResponse_WithNullMessage() {
        // Given
        String field = "testField";
        Object rejectedValue = "testValue";
        String message = null;

        // When
        FieldErrorResponse response = CommonUtils.buildFieldErrorResponse(field, rejectedValue.toString(), message);

        // Then
        assertNotNull(response);
        assertEquals(field, response.getField());
        assertEquals(rejectedValue, response.getRejectedValue());
        assertNull(response.getMessage());
    }

    @Test
    void buildFieldErrorResponse_WithEmptyString() {
        // Given
        String field = "";
        Object rejectedValue = "";
        String message = "";

        // When
        FieldErrorResponse response = CommonUtils.buildFieldErrorResponse(field, rejectedValue.toString(), message);

        // Then
        assertNotNull(response);
        assertEquals("", response.getField());
        assertEquals("", response.getRejectedValue());
        assertEquals("", response.getMessage());
    }

    @Test
    void buildFieldErrorResponse_WithSpecialCharacters() {
        // Given
        String field = "field@#$%^&*()";
        Object rejectedValue = "value@#$%^&*()";
        String message = "message@#$%^&*()";

        // When
        FieldErrorResponse response = CommonUtils.buildFieldErrorResponse(field, rejectedValue.toString(), message);

        // Then
        assertNotNull(response);
        assertEquals("field@#$%^&*()", response.getField());
        assertEquals("value@#$%^&*()", response.getRejectedValue());
        assertEquals("message@#$%^&*()", response.getMessage());
    }

    @Test
    void buildFieldErrorResponse_WithLongValues() {
        // Given
        String field = "a".repeat(1000);
        Object rejectedValue = "b".repeat(1000);
        String message = "c".repeat(1000);

        // When
        FieldErrorResponse response = CommonUtils.buildFieldErrorResponse(field, rejectedValue.toString(), message);

        // Then
        assertNotNull(response);
        assertEquals("a".repeat(1000), response.getField());
        assertEquals("b".repeat(1000), response.getRejectedValue());
        assertEquals("c".repeat(1000), response.getMessage());
    }

    @Test
    void buildFieldErrorResponse_WithNumericValues() {
        // Given
        String field = "numericField";
        Object rejectedValue = 12345;
        String message = "Numeric validation failed";

        // When
        FieldErrorResponse response = CommonUtils.buildFieldErrorResponse(field, rejectedValue.toString(), message);

        // Then
        assertNotNull(response);
        assertEquals("numericField", response.getField());
        assertEquals(12345, response.getRejectedValue());
        assertEquals("Numeric validation failed", response.getMessage());
    }

    @Test
    void buildFieldErrorResponse_WithBooleanValues() {
        // Given
        String field = "booleanField";
        Object rejectedValue = true;
        String message = "Boolean validation failed";

        // When
        FieldErrorResponse response = CommonUtils.buildFieldErrorResponse(field, rejectedValue.toString(), message);

        // Then
        assertNotNull(response);
        assertEquals("booleanField", response.getField());
        assertEquals(true, response.getRejectedValue());
        assertEquals("Boolean validation failed", response.getMessage());
    }

    @Test
    void buildFieldErrorResponse_WithObjectValues() {
        // Given
        String field = "objectField";
        Object rejectedValue = new TestObject();
        String message = "Object validation failed";

        // When
        FieldErrorResponse response = CommonUtils.buildFieldErrorResponse(field, rejectedValue.toString(), message);

        // Then
        assertNotNull(response);
        assertEquals("objectField", response.getField());
        assertEquals(rejectedValue, response.getRejectedValue());
        assertEquals("Object validation failed", response.getMessage());
    }

    // Test class for validation
    public static class TestObject {
        @NotBlank(message = "Name cannot be blank")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        private String name;

        @NotNull(message = "Description cannot be null")
        @Size(max = 255, message = "Description must not exceed 255 characters")
        private String description;

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
