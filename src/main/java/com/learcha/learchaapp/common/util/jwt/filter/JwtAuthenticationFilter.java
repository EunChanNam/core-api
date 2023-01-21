package com.learcha.learchaapp.common.util.jwt.filter;

import com.learcha.learchaapp.auth.service.CustomUserDetailService;
import com.learcha.learchaapp.common.util.jwt.JwtUtil;
import com.learcha.learchaapp.common.util.jwt.model.TokenVerifyResult;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private final UserDetailsService customUserDetailService;

    public JwtAuthenticationFilter(
        AuthenticationManager authenticationManager,
        CustomUserDetailService customUserDetailService
    ) {
        super(authenticationManager);
        this.customUserDetailService = customUserDetailService;
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

        String refreshToken = "";
        for(Cookie cookie : request.getCookies()) {
            if(cookie.getName().equals("refresh_token")) {
                refreshToken = cookie.getValue();
            }
        }

        String token = bearer.substring("Bearer ".length());
        TokenVerifyResult verifyResult = JwtUtil.verifyToken(token);

        if (verifyResult.isVerified()) {
            var user = customUserDetailService.loadUserByUsername(verifyResult.getEmail());
            var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword(),
                user.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            chain.doFilter(request, response);
        } else if(
            ! verifyResult.isVerified() && ! refreshToken.isBlank() && verifyResult.getMessage().equals("Token Expired")) {
            TokenVerifyResult tokenVerifyResult = JwtUtil.verifyToken(refreshToken);

            if(tokenVerifyResult.isVerified()) {
                var user = customUserDetailService.loadUserByUsername(verifyResult.getEmail());

                var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    user.getPassword(),
                    user.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                chain.doFilter(request, response);
            }
        } else {
            logger.error(verifyResult.getMessage());
        }
        chain.doFilter(request, response);
    }
}
