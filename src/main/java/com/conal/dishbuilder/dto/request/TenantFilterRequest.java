package com.conal.dishbuilder.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TenantFilterRequest extends FilterBaseRequest {
    private String name;
    private String domain;
}
