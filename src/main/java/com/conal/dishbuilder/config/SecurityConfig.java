package com.conal.dishbuilder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/", "/login**").permitAll().anyRequest().authenticated()).oauth2Login(oauth -> oauth.loginPage("/login") // Tuỳ chọn: trang login custom
                .defaultSuccessUrl("/user", true) // Sau khi đăng nhập thành công
        ).logout(logout -> logout.logoutSuccessUrl("/") // Sau khi logout sẽ về trang chủ
                .invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID"));

        return http.build();
    }
}