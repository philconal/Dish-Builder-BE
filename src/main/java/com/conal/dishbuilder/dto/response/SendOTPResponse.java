package com.conal.dishbuilder.dto.response;

import com.conal.dishbuilder.constant.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(builderMethodName = "newBuilder", setterPrefix = "set")
public class SendOTPResponse {
    private ErrorType errorType;
    private boolean isCanSend;
}
