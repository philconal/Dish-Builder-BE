package com.conal.dishbuilder.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginResponse {
    private String accessToken;
    @JsonIgnore
    private String refreshToken;
    private List<String> roles;
}
