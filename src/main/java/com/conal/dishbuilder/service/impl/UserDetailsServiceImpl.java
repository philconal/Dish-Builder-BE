package com.conal.dishbuilder.service.impl;

import com.conal.dishbuilder.constant.CommonStatus;
import com.conal.dishbuilder.context.TenantContextHolder;
import com.conal.dishbuilder.domain.UserEntity;
import com.conal.dishbuilder.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UUID tenantId = TenantContextHolder.getTenantContext();
        log.info("Loading user: '{}' for tenant: '{}'", username, tenantId);

        // First try to find user in the current tenant context
        UserEntity userEntity = userRepository.findByUsernameAndTenantIdAndStatus(username, tenantId, CommonStatus.ACTIVE)
                .orElse(null);

        // If not found in current tenant, try to find across all tenants
        if (userEntity == null) {
            log.debug("User '{}' not found in tenant '{}', searching across all tenants", username, tenantId);
            userEntity = userRepository.findByUsernameAndStatus(username, CommonStatus.ACTIVE)
                    .orElseThrow(() -> {
                        log.warn("User '{}' not found in any tenant", username);
                        return new UsernameNotFoundException("User '" + username + "' not found");
                    });
            
            // Update tenant context to the user's actual tenant
            TenantContextHolder.setTenantContext(userEntity.getTenantId());
            log.info("Found user '{}' in tenant '{}', updated tenant context", username, userEntity.getTenantId());
        }

        List<SimpleGrantedAuthority> authorities = userEntity.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();

        log.debug("User '{}' has roles: {}", username, authorities);

        return User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
