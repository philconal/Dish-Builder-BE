package com.conal.dishbuilder.service.impl;

import com.conal.dishbuilder.context.TenantContextHolder;
import com.conal.dishbuilder.domain.UserEntity;
import com.conal.dishbuilder.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@AllArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UUID tenantId = TenantContextHolder.getTenantContext();
        log.info("tenantContext: {}", tenantId);
        UserEntity userEntity = userRepository.findByUsernameAndTenantId(username, tenantId)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        List<SimpleGrantedAuthority> authorities = userEntity.getRoles().stream()
                .map(item -> new SimpleGrantedAuthority(item.getName())).toList();

        return User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .build();
    }
}
