package com.conal.dishbuilder.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {
    private String code;
    private String message;
    private String details;
    private List<FieldErrorResponse> fieldErrors;
}
