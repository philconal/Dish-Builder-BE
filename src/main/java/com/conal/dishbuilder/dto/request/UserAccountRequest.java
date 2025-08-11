package com.conal.dishbuilder.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "set")
public class UserAccountRequest {

    @NotBlank(message = "Username không được để trống")
    @Size(max = 255)
    private String username;

    @NotBlank(message = "Password không được để trống")
    @Size(min = 6, max = 255, message = "Password từ 6 đến 255 ký tự")
    private String password;

    @Size(max = 255)
    private String logoUrl;

    @NotBlank(message = "First name không được để trống")
    @Size(max = 255)
    private String firstName;

    @NotBlank(message = "Last name không được để trống")
    @Size(max = 255)
    private String lastName;

    @NotBlank(message = "Phone không được để trống")
    @Size(max = 255)
    private String phone;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 255)
    private String email;

    private Integer registerWith;

    @NotNull(message = "TenantId không được để trống")
    private UUID tenantId;

    private Set<UUID> roleIds;
}
