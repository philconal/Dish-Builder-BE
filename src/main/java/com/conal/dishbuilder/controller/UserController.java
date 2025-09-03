package com.conal.dishbuilder.controller;

import com.conal.dishbuilder.constant.Constants;
import com.conal.dishbuilder.dto.request.RegisterUserRequest;
import com.conal.dishbuilder.dto.request.UpdateUserRequest;
import com.conal.dishbuilder.dto.request.filter.UserFilterRequest;
import com.conal.dishbuilder.dto.response.BaseResponse;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.dto.response.UserResponse;
import com.conal.dishbuilder.dto.response.UserInfoResponse;
import com.conal.dishbuilder.config.security.RequireSuperAdmin;
import com.conal.dishbuilder.config.security.RequireAdminOrSuperAdmin;
import com.conal.dishbuilder.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<BaseResponse<Boolean>> registerUser(@RequestBody RegisterUserRequest userRequest) {
        boolean registered = userService.registerAccount(userRequest);
        return ResponseEntity.ok().body(BaseResponse.ok(registered));
    }

    @GetMapping("/all")
    @RequireSuperAdmin
    public ResponseEntity<BaseResponse<PageResponse<UserResponse>>> getAllUsers(@ModelAttribute UserFilterRequest filter, Pageable pageable) {
        var allUsers = userService.findAllUsers(filter, pageable);
        return ResponseEntity.ok().body(BaseResponse.ok(allUsers));
    }

    @PutMapping("/update-profile")
    @RequireAdminOrSuperAdmin
    public ResponseEntity<BaseResponse<Boolean>> updateUserInfo(@RequestBody UpdateUserRequest request) {
        boolean registered = userService.updateUserProfile(request);
        return ResponseEntity.ok(BaseResponse.ok(registered));
    }

    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserInfoResponse>> getCurrentUserInfo() {
        UserInfoResponse userInfo = userService.getCurrentUserInfo();
        return ResponseEntity.ok(BaseResponse.ok(userInfo));
    }
}
