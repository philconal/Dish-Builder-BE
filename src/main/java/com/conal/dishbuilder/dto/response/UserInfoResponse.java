package com.conal.dishbuilder.dto.response;

import com.conal.dishbuilder.constant.CommonStatus;
import com.conal.dishbuilder.constant.UserType;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponse {
    private UUID id;
    private String username;
    private String logoUrl;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Integer registerWith;
    private CommonStatus status;
    private UserType userType;
    private String tenant;
    private List<String> roles;
}
