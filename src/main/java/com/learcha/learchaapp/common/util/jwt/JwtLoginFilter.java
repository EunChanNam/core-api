package com.learcha.learchaapp.common.util.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learcha.learchaapp.auth.service.CustomUserDetailService;
import com.learcha.learchaapp.auth.web.AuthDto.LoginDto;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    ) throws IOException {
        UserDetailsImpl principal = (UserDetailsImpl) authResult.getPrincipal();
        JWTTokenInfo tokenInfo = JwtProvider.generateTokenInfo(principal);

        userDetailService.registerRefreshToken(principal.getMember(), tokenInfo.getRefreshToken());

        response.setHeader("Authorization", "Bearer " + tokenInfo.getAccessToken());
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(tokenInfo));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, AuthenticationException failed)
        throws IOException, ServletException {
        failed.getMessage();

        super.unsuccessfulAuthentication(request, response, failed);
    }
}
