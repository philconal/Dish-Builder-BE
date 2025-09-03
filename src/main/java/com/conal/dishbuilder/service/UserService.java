package com.conal.dishbuilder.service;

import com.conal.dishbuilder.dto.request.RegisterUserRequest;
import com.conal.dishbuilder.dto.request.UpdateUserRequest;
import com.conal.dishbuilder.dto.request.filter.UserFilterRequest;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.dto.response.UserResponse;
import com.conal.dishbuilder.dto.response.UserInfoResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    boolean registerAccount(RegisterUserRequest request);
    PageResponse<UserResponse> findAllUsers(UserFilterRequest request, Pageable pageable);
    boolean updateUserProfile(UpdateUserRequest request);
    UserInfoResponse getCurrentUserInfo();
}
