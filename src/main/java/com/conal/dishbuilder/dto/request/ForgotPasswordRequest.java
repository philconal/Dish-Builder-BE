package com.conal.dishbuilder.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @NotBlank(message = "Username must not be empty")
    private String username;
}
