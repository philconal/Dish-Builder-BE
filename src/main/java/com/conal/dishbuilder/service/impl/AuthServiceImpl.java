package com.conal.dishbuilder.service.impl;

import com.conal.dishbuilder.dto.request.LoginRequest;
import com.conal.dishbuilder.dto.request.LoginResponse;
import com.conal.dishbuilder.exception.BadRequestException;
import com.conal.dishbuilder.service.AuthService;
import com.conal.dishbuilder.util.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    public LoginResponse login(LoginRequest request) {
        var token = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        var authenticate = authenticationManager.authenticate(token);

        if (!authenticate.isAuthenticated()) {
            throw new BadRequestException("Invalid username or password");
        }
        UserDetails userDetails = (UserDetails) authenticate.getPrincipal();

        String accessToken = jwtUtils.generateAccessToken(userDetails);
        return LoginResponse.builder()
                .accessToken(accessToken)
                .build();
    }

    @Override
    @Transactional
    public LoginResponse getAccessToken(String refreshToken) {
        if (!jwtUtils.validateToken(refreshToken)) {
            return null;
        }

        return LoginResponse.builder()
                .build();
    }

    @Override
    public boolean logout(String refreshToken) {

        return true;
    }


}
