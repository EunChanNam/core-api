package com.learcha.learchaapp.common.util.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learcha.learchaapp.auth.service.CustomUserDetailService;
import com.learcha.learchaapp.auth.web.AuthDto.LoginDto;
import com.learcha.learchaapp.common.util.jwt.JwtUtil;
import com.learcha.learchaapp.common.util.jwt.model.JwtTokenBox;
import com.learcha.learchaapp.common.util.jwt.model.UserDetailsImpl;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final CustomUserDetailService userDetailService;

    public JwtLoginFilter(
        AuthenticationManager authenticationManager,
        ObjectMapper objectMapper,
        CustomUserDetailService customUserDetailService
    ) {
        super(authenticationManager);
        this.objectMapper = objectMapper;
        this.userDetailService = customUserDetailService;
        this.setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws AuthenticationException {
        try {
            LoginDto dto = objectMapper.readValue(request.getInputStream(), LoginDto.class);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                dto.getEmail(),
                dto.getPassword(),
                null
            );
            return getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void successfulAuthentication(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain,
        Authentication authResult
    ) {
        UserDetailsImpl principal = (UserDetailsImpl) authResult.getPrincipal();
        JwtTokenBox tokenBox = JwtUtil.generateTokenInfo(principal);

        String refreshToken = tokenBox.getRefreshToken();
        userDetailService.registerRefreshToken(principal.getMember(), refreshToken);

        response.setHeader("Authorization", "Bearer " + tokenBox.getAccessToken());
        response.setHeader("Set-Cookie", createCookieOfRefreshToken(refreshToken).toString());
    }

    @Override
    protected void unsuccessfulAuthentication(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException failed
    ) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }


    private ResponseCookie createCookieOfRefreshToken(String refreshToken) {
        ResponseCookieBuilder refreshTokenCookie = ResponseCookie.from("refresh_cookie", refreshToken);
        refreshTokenCookie.httpOnly(true);
        refreshTokenCookie.sameSite("None");
        return refreshTokenCookie.build();
    }
 }
