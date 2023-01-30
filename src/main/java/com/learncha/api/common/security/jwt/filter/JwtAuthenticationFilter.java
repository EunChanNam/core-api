package com.learncha.api.common.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learncha.api.auth.service.CustomUserDetailService;
import com.learncha.api.auth.web.AuthDto.LoginSuccessResponse;
import com.learncha.api.common.security.jwt.model.TokenVerifyResult;
import com.learncha.api.common.security.jwt.JwtUtil;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private static final String COOKIE_NAME = "refresh_token";
    private final UserDetailsService customUserDetailService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(
        AuthenticationManager authenticationManager,
        CustomUserDetailService customUserDetailService,
        ObjectMapper objectMapper
    ) {
        super(authenticationManager);
        this.customUserDetailService = customUserDetailService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        String bearer = request.getHeader("Authorization");

        if (bearer == null || !bearer.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = bearer.substring("Bearer ".length());
        TokenVerifyResult verifyResult = JwtUtil.verifyToken(token);

        if(verifyResult.isVerified()) {
            setAuthentication(verifyResult);
            chain.doFilter(request, response);
        } else if(verifyResult.getMessage().equals("Token Expired")) {
            String refreshToken = getRefreshToken(request);

            if(StringUtils.isBlank(refreshToken))
                chain.doFilter(request, response);

            TokenVerifyResult refreshTokenVerifyResult = JwtUtil.verifyToken(refreshToken);

            if(refreshTokenVerifyResult.isVerified()) {
                UserDetails member = customUserDetailService.loadUserByUsername(refreshTokenVerifyResult.getEmail());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    member.getUsername(),
                    member.getPassword(),
                    member.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
                onSuccessfulAuthentication(request, response, authentication);
            }
//            setAuthentication(refreshTokenVerifyResult);
            chain.doFilter(request, response);
        } else {
            logger.error(verifyResult.getMessage());
        }
        chain.doFilter(request, response);
    }

    /**
      인증 만료시 어떻게 할지 논의 필`
     */
    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response, Authentication authResult) throws IOException {
        String email = (String) authResult.getPrincipal();
        String accessToken = JwtUtil.generateAccessTokenFromUserEmail(email);

        LoginSuccessResponse res = LoginSuccessResponse.builder()
            .email(email)
            .authType("EMAIL")
            .accessToken(accessToken).build();

        response.getWriter().write(objectMapper.writeValueAsString(res));
    }

    private void setAuthentication(TokenVerifyResult verifyResult) {
        UserDetails member = customUserDetailService.loadUserByUsername(verifyResult.getEmail());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            member.getUsername(),
            member.getPassword(),
            member.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    private String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies.length == 0) {
            return "";
        }

        for(Cookie cookie : cookies)
            if(cookie.getName().equals(COOKIE_NAME))
                return cookie.getValue();

        return "";
    }
}
