package com.learncha.api.common.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.learncha.api.auth.service.CustomUserDetailService;
import com.learncha.api.common.security.jwt.filter.JwtAuthenticationFilter;
import com.learncha.api.common.security.jwt.filter.JwtLoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class HttpCustomConfigure extends AbstractHttpConfigurer<HttpCustomConfigure, HttpSecurity> {

    private final ObjectMapper objectMapper;
    private final CustomUserDetailService userDetailService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);

        http
            .addFilterAt(
                new JwtLoginFilter(authenticationManager, userDetailService, objectMapper),
                UsernamePasswordAuthenticationFilter.class
            )
            .addFilterAt(new JwtAuthenticationFilter(authenticationManager, userDetailService, objectMapper),
                UsernamePasswordAuthenticationFilter.class
            )
            ;
    }

    public static HttpCustomConfigure customDsl(ObjectMapper objectMapper, CustomUserDetailService userDetailService) {
        return new HttpCustomConfigure(objectMapper, userDetailService);
    }
}
