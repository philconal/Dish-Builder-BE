package com.conal.dishbuilder.controller;

import com.conal.dishbuilder.constant.Constants;
import com.conal.dishbuilder.dto.request.LoginRequest;
import com.conal.dishbuilder.dto.request.LoginResponse;
import com.conal.dishbuilder.dto.request.UserAccountRequest;
import com.conal.dishbuilder.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = Constants.Endpoint.AUTH)
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/user")
    public Map<String, Object> oauthLogin(@AuthenticationPrincipal OAuth2User principal) {
        //GOOGLE
        UserAccountRequest.builder()
                .setUsername(principal.getAttributes().get("email").toString())
                .setPassword("")
                .build();

        return principal.getAttributes();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse login = authService.login(request);
        return ResponseEntity.ok(Map.of("access_token", login.getAccessToken()));
    }

}
