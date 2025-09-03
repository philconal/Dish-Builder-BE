package com.conal.dishbuilder.dto.request;

import com.conal.dishbuilder.constant.CommonStatus;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateTenantRequest {
    @Nonnull
    private UUID id;

    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Pattern(regexp = "^[0-9]{9,15}$", message = "Phone must be between 9 and 15 digits")
    private String phone;

    @Email(message = "Email format is invalid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Size(max = 255, message = "Logo URL must not exceed 255 characters")
    private String logoUrl;

    private CommonStatus status;
}
