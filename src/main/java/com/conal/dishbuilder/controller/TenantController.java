package com.conal.dishbuilder.controller;

import com.conal.dishbuilder.constant.Constants;
import com.conal.dishbuilder.dto.request.CreateTenantRequest;
import com.conal.dishbuilder.dto.response.BaseResponse;
import com.conal.dishbuilder.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.Endpoint.TENANT)
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<BaseResponse<Boolean>> registerTenant(@RequestBody CreateTenantRequest request) {
        boolean registered = tenantService.registerTenant(request);
        return ResponseEntity.ok(BaseResponse.ok(registered));
    }
}
