package com.learncha.api.common.config;

import com.learncha.api.auth.service.GoogleLoginService;
import com.learncha.api.common.security.jwt.filter.JwtAuthenticationFilter;
import com.learncha.api.common.security.jwt.handler.CustomOAuth2LoginSuccessHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final GoogleLoginService googleLoginService;
    private final CustomOAuth2LoginSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .formLogin().disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/v1/auth").permitAll()
                .antMatchers(HttpMethod.POST,  "/api/v1/auth/logout").authenticated()
                .antMatchers(HttpMethod.GET, "/api/v1/auth").permitAll()
                .antMatchers(HttpMethod.GET,  "/api/v1/auth/access-token").permitAll()
                .antMatchers(HttpMethod.PUT,  "/api/v1/auth").authenticated()
                .antMatchers(HttpMethod.DELETE,  "/api/v1/auth").authenticated()
                .antMatchers(HttpMethod.GET,  "/api/v1/auth/temporary-password").permitAll()
                .antMatchers("/api/**").authenticated()
            .and()
            .addFilterAt(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling()
            .and()
            .oauth2Login()
            .userInfoEndpoint()
            .userService(googleLoginService)
            .and()
            .successHandler(successHandler)
            ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
            "/api/health-check",
            "/api/v1/auth/login",
            "/api/v1/auth/send-code",
            "/api/v1/auth/confirm-code"
        );
    }
}

