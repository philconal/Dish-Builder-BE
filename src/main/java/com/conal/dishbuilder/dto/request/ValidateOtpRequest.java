package com.conal.dishbuilder.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ValidateOtpRequest {

    @NotBlank(message = "Otp must not be blank")
    private String otp;
    @NotBlank(message = "SessionId must not be empty")
    private String sessionId;
}
