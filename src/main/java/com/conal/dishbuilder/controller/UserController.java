package com.conal.dishbuilder.controller;

import com.conal.dishbuilder.constant.Constants;
import com.conal.dishbuilder.dto.request.RegisterUserRequest;
import com.conal.dishbuilder.dto.response.BaseResponse;
import com.conal.dishbuilder.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(Constants.Endpoint.USER)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //test only
    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttributes(); // Trả về thông tin người dùng từ Google
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Boolean>> registerUser(HttpServletRequest request, @RequestBody RegisterUserRequest userRequest) {
        var tenantId = (UUID) request.getAttribute("tenantId");
        userRequest.setTenantId(tenantId);
        boolean registered = userService.registerAccount(userRequest);
        return ResponseEntity.ok().body(BaseResponse.<Boolean>builder()
                .setStatus(HttpStatus.OK.value())
                .setData(registered).build());
    }
}
