package com.conal.dishbuilder.controller;

import com.conal.dishbuilder.constant.Constants;
import com.conal.dishbuilder.dto.request.UserAccountRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = Constants.Endpoint.AUTH)
public class AuthController {
    @GetMapping("/user")
    public Map<String, Object> oauthLogin(@AuthenticationPrincipal OAuth2User principal) {
        //GOOGLE
        UserAccountRequest.builder()
                .setUsername(principal.getAttributes().get("email").toString())
                .setPassword("")
                .build();

        return principal.getAttributes();
    }
}
