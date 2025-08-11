package com.conal.dishbuilder.service.impl;

import com.conal.dishbuilder.dto.request.UserAccountRequest;
import com.conal.dishbuilder.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public boolean registerAccount(UserAccountRequest request) {
        return false;
    }
}
