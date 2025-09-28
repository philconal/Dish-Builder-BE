package com.conal.dishbuilder.config;

import com.conal.dishbuilder.constant.CommonStatus;
import com.conal.dishbuilder.constant.TokenType;
import com.conal.dishbuilder.context.TenantContextHolder;
import com.conal.dishbuilder.context.UserContextHolder;
import com.conal.dishbuilder.domain.TenantEntity;
import com.conal.dishbuilder.exception.NotFoundException;
import com.conal.dishbuilder.service.TenantService;
import com.conal.dishbuilder.util.JwtUtils;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final TenantService tenantService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) {
        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            username = jwtUtils.getUsername(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Set default tenant context for authentication
                TenantEntity defaultTenant = tenantService.findDefaultTenant();
                TenantContextHolder.setTenantContext(defaultTenant.getId());

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtUtils.validateToken(token) && !jwtUtils.existsTokenInBlacklist(token, TokenType.ACCESS)) {
                    var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(userDetails);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                log.error("Error during authentication: {}", e.getMessage());
            }
        }

        try {
            // Always set default tenant context for the request
            TenantEntity tenant = tenantService.findDefaultTenant();
            TenantContextHolder.setTenantContext(tenant.getId());
            UserContextHolder.setUserContext(username);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error in JwtRequestFilter: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            try {
                response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            TenantContextHolder.clearTenantContext();
            UserContextHolder.clearUserContext();
        }
    }
}
