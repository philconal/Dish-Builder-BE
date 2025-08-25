package com.conal.dishbuilder.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TenantResponse {
    private UUID id;
    private String name;
    private String phone;
    private String email;
    private String urlSlug;
    private String subDomain;
    private String logoUrl;
}