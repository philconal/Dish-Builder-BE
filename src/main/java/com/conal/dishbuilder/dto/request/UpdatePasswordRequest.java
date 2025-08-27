package com.conal.dishbuilder.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordRequest {
    @NotBlank(message = "Password is required")
    @Size(max = 100, message = "Password must not exceed 100 characters")
    private String password;
    @NotBlank(message = "Confirmed password is required")
    @Size(max = 100, message = "Confirmed password must not exceed 100 characters")
    private String confirmedPassword;
    @AssertTrue(message = "Passwords do not match")
    private boolean isPasswordsMatching() {
        return password != null && password.equals(confirmedPassword);
    }
    @NotBlank(message = "SessionId must not be empty")
    private String sessionId;
}
