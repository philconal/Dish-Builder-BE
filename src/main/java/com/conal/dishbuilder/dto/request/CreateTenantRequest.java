package com.conal.dishbuilder.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import javax.xml.transform.Source;

@Data
@Builder
public class CreateTenantRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9]{9,15}$", message = "Phone must be between 9 and 15 digits")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @NotBlank(message = "URL Slug is required")
    @Size(max = 255, message = "URL Slug must not exceed 255 characters")
    private String urlSlug;

    @Size(max = 255, message = "Logo URL must not exceed 255 characters")
    private String logoUrl;

    @Size(max = 255, message = "Sub Domain must not exceed 255 characters")
    private String subDomain;

}
