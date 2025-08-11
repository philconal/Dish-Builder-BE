package com.conal.dishbuilder.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(setterPrefix = "set")
public class FieldErrorResponse {
    private String field;
    private String rejectedValue;
    private String message;
}
