package com.conal.dishbuilder.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FilterBaseRequest extends BaseRequest {
    private boolean ignorePaging;
}
