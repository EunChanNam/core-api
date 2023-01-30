package com.learncha.api.common.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learncha.api.auth.service.CustomUserDetailService;
import com.learncha.api.auth.web.AuthDto.LoginDto;
import com.learncha.api.auth.web.AuthDto.LoginSuccessResponse;
import com.learncha.api.common.security.jwt.model.JwtTokenBox;
import com.learncha.api.common.security.jwt.model.UserDetailsImpl;
import com.learncha.api.common.security.jwt.JwtUtil;
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
        CustomUserDetailService customUserDetailService,
        ObjectMapper objectMapper
    ) {
        super(authenticationManager);
        this.userDetailService = customUserDetailService;
        this.objectMapper = objectMapper;
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
    ) throws IOException {
        UserDetailsImpl principal = (UserDetailsImpl) authResult.getPrincipal();
        JwtTokenBox tokenBox = JwtUtil.generateTokenInfo(principal);

        String refreshToken = tokenBox.getRefreshToken();
        userDetailService.registerRefreshToken(principal.getMember(), refreshToken);

        LoginSuccessResponse res = LoginSuccessResponse.builder()
            .email(principal.getUsername())
            .authType(principal.getMember().getAuthType().getDescription())
            .accessToken(tokenBox.getAccessToken()).build();

        response.setHeader("Set-Cookie", createCookieOfRefreshToken(refreshToken).toString());
        response.getWriter().write(objectMapper.writeValueAsString(res));
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
        ResponseCookieBuilder refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken);
        refreshTokenCookie.httpOnly(true);
        refreshTokenCookie.path("/");
        refreshTokenCookie.sameSite("None");
        return refreshTokenCookie.build();
    }
 }
