package com.conal.dishbuilder.dto.response;

import com.conal.dishbuilder.constant.ErrorType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName = "newBuilder", setterPrefix = "set")
public class SendOTPResponse {
    private ErrorType errorType;
    private boolean isCanSend;

}
