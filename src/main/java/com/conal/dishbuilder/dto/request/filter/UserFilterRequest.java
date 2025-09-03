package com.conal.dishbuilder.dto.request.filter;

import com.conal.dishbuilder.constant.CommonStatus;
import com.conal.dishbuilder.dto.request.FilterBaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserFilterRequest extends FilterBaseRequest {
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private CommonStatus status;
}
