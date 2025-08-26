package com.conal.dishbuilder.service;


import com.conal.dishbuilder.dto.request.LoginRequest;
import com.conal.dishbuilder.dto.request.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
    boolean forgotPassword(String username);
    boolean validateOtp(String otp);
    boolean logout(String refreshToken);
}
