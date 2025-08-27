package com.conal.dishbuilder.service.impl;

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

        UserEntity userEntity = userRepository.findByUsernameAndTenantId(username, tenantId)
                .orElseThrow(() -> {
                    log.warn("User '{}' not found for tenant '{}'", username, tenantId);
                    return new UsernameNotFoundException("User '" + username + "' not found for tenant: " + tenantId);
                });

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
