package com.conal.dishbuilder.service;

import com.conal.dishbuilder.dto.request.RegisterUserRequest;
import com.conal.dishbuilder.dto.request.UserAccountRequest;

public interface UserService {
    boolean registerAccount(RegisterUserRequest request);

}
