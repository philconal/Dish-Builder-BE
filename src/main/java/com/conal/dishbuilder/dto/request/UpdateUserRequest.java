package com.conal.dishbuilder.dto.request;

import com.conal.dishbuilder.constant.CommonStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    @NonNull
    private UUID id;
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;
    @Pattern(regexp = "^[0-9]{9,15}$", message = "Phone must be between 9 and 15 digits")
    private String phone;
    @Size(max = 255, message = "Logo URL must not exceed 255 characters")
    private String avtUrl;
    private CommonStatus status;
}
