package com.conal.dishbuilder.service;


import com.conal.dishbuilder.dto.request.LoginRequest;
import com.conal.dishbuilder.dto.request.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
    LoginResponse getAccessToken(String refreshToken);

    boolean logout(String refreshToken);
}
