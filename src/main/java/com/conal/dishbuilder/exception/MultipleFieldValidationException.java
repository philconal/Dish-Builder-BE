package com.conal.dishbuilder.exception;

import com.conal.dishbuilder.dto.response.FieldErrorResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class MultipleFieldValidationException extends RuntimeException {

    private final List<FieldErrorResponse> fieldErrors;

    public MultipleFieldValidationException(List<FieldErrorResponse> errors) {
        this.fieldErrors = errors;
    }

}
