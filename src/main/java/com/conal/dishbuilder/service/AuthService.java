package com.conal.dishbuilder.service;


import com.conal.dishbuilder.dto.request.LoginRequest;
import com.conal.dishbuilder.dto.request.LoginResponse;
import com.conal.dishbuilder.dto.request.UpdatePasswordRequest;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    String forgotPassword(String username);

    boolean validateOtp(String sessionId, String otp);

    boolean logout(String refreshToken);

    boolean updatePassword(UpdatePasswordRequest request);

}
