package com.learcha.learchaapp.common.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.learcha.learchaapp.auth.service.CustomUserDetailService;
import com.learcha.learchaapp.common.util.jwt.JwtAuthenticationFilter;
import com.learcha.learchaapp.common.util.jwt.JwtLoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {

    private final ObjectMapper objectMapper;
    private final CustomUserDetailService userDetailService;

    @Override
    public void configure(HttpSecurity http) {

        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);

        http
            .addFilterAt(
                new JwtLoginFilter(authenticationManager, objectMapper, userDetailService),
                UsernamePasswordAuthenticationFilter.class
            )
            .addFilterAt(new JwtAuthenticationFilter(authenticationManager, userDetailService),
                UsernamePasswordAuthenticationFilter.class
            );
    }

    public static MyCustomDsl customDsl(ObjectMapper objectMapper, CustomUserDetailService userDetailService) {
        return new MyCustomDsl(objectMapper, userDetailService);
    }
}
