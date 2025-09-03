package com.conal.dishbuilder.dto.request;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FilterBaseRequest {
    private boolean ignorePaging;
    private UUID tenantId;
}
