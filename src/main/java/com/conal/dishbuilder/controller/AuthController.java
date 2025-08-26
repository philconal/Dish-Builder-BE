package com.conal.dishbuilder.controller;

import com.conal.dishbuilder.constant.Constants;
import com.conal.dishbuilder.dto.request.*;
import com.conal.dishbuilder.dto.response.BaseResponse;
import com.conal.dishbuilder.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
    public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse login = authService.login(request);
        return ResponseEntity.ok(BaseResponse.ok(login));
    }

    @PostMapping("forgot-password")
    public ResponseEntity<BaseResponse<Boolean>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        boolean forgotPassword = authService.forgotPassword(request.getUsername());
        return ResponseEntity.ok(BaseResponse.ok(forgotPassword));
    }

    @PostMapping("validate-otp")
    public ResponseEntity<BaseResponse<Boolean>> validateOtp(@Valid @RequestBody ValidateOtpRequest request) {
        boolean validateOtp = authService.validateOtp(request.getOtp());
        return ResponseEntity.ok(BaseResponse.ok(validateOtp));
    }

    @PostMapping("update-password")
    public ResponseEntity<BaseResponse<Boolean>> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        boolean isUpdated = authService.updatePassword(request);
        return ResponseEntity.ok(BaseResponse.ok(isUpdated));
    }
}
