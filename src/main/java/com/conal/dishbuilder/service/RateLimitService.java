package com.conal.dishbuilder.service;

import com.conal.dishbuilder.dto.response.SendOTPResponse;

public interface RateLimitService {
    SendOTPResponse canSendOtp(String ...params);
    void logSuccessfullySent(String ...params);
}
