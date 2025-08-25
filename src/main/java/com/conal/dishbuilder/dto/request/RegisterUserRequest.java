package com.conal.dishbuilder.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserRequest extends BaseRequest {
    @NotBlank(message = "Username is required")
    @Size(max = 100, message = "Username must not exceed 100 characters")
    private String username;
    @NotBlank(message = "Password is required")
    @Size(max = 100, message = "Password must not exceed 100 characters")
    private String password;
    @NotBlank(message = "Confirmed password is required")
    @Size(max = 100, message = "Confirmed password must not exceed 100 characters")
    private String confirmedPassword;
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;
    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9]{9,15}$", message = "Phone must be between 9 and 15 digits")
    private String phone;
    @Size(max = 255, message = "Avatar URL must not exceed 255 characters")
    private String avtUrl;
    @AssertTrue(message = "Passwords do not match")
    private boolean isPasswordsMatching() {
        return password != null && password.equals(confirmedPassword);
    }
}
