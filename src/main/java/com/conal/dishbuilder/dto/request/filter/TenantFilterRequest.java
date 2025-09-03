package com.conal.dishbuilder.dto.request.filter;

import com.conal.dishbuilder.dto.request.FilterBaseRequest;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TenantFilterRequest extends FilterBaseRequest {
    private String name;
    private String domain;
}
