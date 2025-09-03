package com.conal.dishbuilder.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TenantCheckoutRequest {
    @NotBlank(message = "Company Email is required.")
    @Size(message = "Maximum name length is 255.")
    @Email(message = "Invalid email format.")
    private String companyEmail;
    @NotBlank(message = "Company name is required.")
    @Size(message = "Maximum name length is 255.")
    private String companyName;
    private String subDomain;
}
