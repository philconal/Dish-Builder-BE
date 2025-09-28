package com.conal.dishbuilder.controller;

import com.conal.dishbuilder.constant.Constants;
import com.conal.dishbuilder.dto.request.*;
import com.conal.dishbuilder.dto.response.BaseResponse;
import com.conal.dishbuilder.service.AuthService;
import com.conal.dishbuilder.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping(value = Constants.Endpoint.AUTH)
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
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

        ResponseCookie cookie = ResponseCookie.from("refreshToken", login.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/") //allow all domains (refresh, logout)
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();
        return ResponseEntity
                .ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(BaseResponse.ok(login));
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Boolean>> register(@RequestBody RegisterUserRequest request) {
        boolean registered = userService.registerAccount(request);
        return ResponseEntity.ok().body(BaseResponse.ok(registered));
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<LoginResponse>> refresh  (@RequestHeader("Authorization") String authHeader,
    @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        log.info("Refresh token attempt: {}", refreshToken != null ? "token received" : "no token found");
        String accessToken = authHeader.replace("Bearer ", "");
        LoginResponse login = authService.refreshToken(refreshToken, accessToken);
        return ResponseEntity.ok(BaseResponse.ok(login));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<BaseResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String sessionId = authService.forgotPassword(request.getUsername());
        return ResponseEntity.ok(BaseResponse.ok(sessionId));
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<BaseResponse<Boolean>> validateOtp(@Valid @RequestBody ValidateOtpRequest request) {
        log.info("Validate OTP for sessionId: {}", request.getSessionId());
        boolean validateOtp = authService.validateOtp(request.getSessionId(), request.getOtp());
        return ResponseEntity.ok(BaseResponse.ok(validateOtp));
    }

    @PostMapping("/update-password")
    public ResponseEntity<BaseResponse<Boolean>> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        boolean isUpdated = authService.updatePassword(request);
        return ResponseEntity.ok(BaseResponse.ok(isUpdated));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String authHeader,
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {

        String accessToken = authHeader.replace("Bearer ", "");

        authService.logout(refreshToken, accessToken);

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body("Logged out");
    }


}
