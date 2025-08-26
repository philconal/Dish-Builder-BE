package com.conal.dishbuilder.config;

import com.conal.dishbuilder.context.TenantContextHolder;
import com.conal.dishbuilder.context.UserContextHolder;
import com.conal.dishbuilder.domain.TenantEntity;
import com.conal.dishbuilder.domain.UserEntity;
import com.conal.dishbuilder.repository.TenantRepository;
import com.conal.dishbuilder.repository.UserRepository;
import com.conal.dishbuilder.service.TenantService;
import com.conal.dishbuilder.util.JwtUtils;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final TenantService tenantService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            username = jwtUtils.getUsername(token);
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtils.validateToken(token)) {
                var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                auth.setDetails(userDetails);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        try {
            // get the domain name of each tenant
            String serverName = request.getServerName();
            TenantEntity tenant = tenantService.findBySubDomain(serverName);
            TenantContextHolder.setTenantContext(tenant.getId());

            UserContextHolder.setUserContext("conal");
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error in JwtRequestFilter: {}", e.getMessage());
        } finally {
            TenantContextHolder.clearTenantContext();
        }
    }
}
