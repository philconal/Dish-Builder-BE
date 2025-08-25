package com.conal.dishbuilder.controller;

import com.conal.dishbuilder.constant.Constants;
import com.conal.dishbuilder.dto.request.CreateTenantRequest;
import com.conal.dishbuilder.dto.request.TenantFilterRequest;
import com.conal.dishbuilder.dto.request.UpdateTenantRequest;
import com.conal.dishbuilder.dto.response.BaseResponse;
import com.conal.dishbuilder.dto.response.PageResponse;
import com.conal.dishbuilder.dto.response.TenantResponse;
import com.conal.dishbuilder.service.TenantService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(Constants.Endpoint.TENANT)
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Boolean>> registerTenant(@RequestBody CreateTenantRequest request) {
        boolean registered = tenantService.registerTenant(request);
        return ResponseEntity.ok(BaseResponse.ok(registered));
    }

    @PutMapping("/update-profile")
    public ResponseEntity<BaseResponse<Boolean>> updateTenantInfo(@RequestBody UpdateTenantRequest request) {
        boolean registered = tenantService.updateTenant(request);
        return ResponseEntity.ok(BaseResponse.ok(registered));
    }

    @GetMapping("/all")
    public ResponseEntity<BaseResponse<PageResponse<TenantResponse>>> getAll(@ModelAttribute TenantFilterRequest filter, Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.ok(tenantService.findAll(filter, pageable)));
    }
}
