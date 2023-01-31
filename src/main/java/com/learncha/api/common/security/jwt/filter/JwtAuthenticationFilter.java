package com.learncha.api.common.security.jwt.filter;

import com.learncha.api.common.security.jwt.model.JWTManager;
import com.learncha.api.common.security.jwt.model.JWTManager.TokenVerifyResult;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String REFRESH_COOKIE_NAME = "refresh_token";
    private final UserDetailsService customUserDetailService;
    private final JWTManager jwtManager;

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
        TokenVerifyResult verifyResult = jwtManager.verifyToken(token);

        if(verifyResult.isVerified()) {
            setAuthentication(verifyResult);
            chain.doFilter(request, response);
        } else if(verifyResult.getMessage().equals(TokenVerifyResult.TOKEN_EXPIRED_MESSAGE)) {
            String refreshToken = getRefreshToken(request);

            if(StringUtils.isBlank(refreshToken))
                chain.doFilter(request, response);

            TokenVerifyResult refreshTokenVerifyResult = jwtManager.verifyToken(refreshToken);

            if(refreshTokenVerifyResult.isVerified()) {
                UserDetails member = customUserDetailService.loadUserByUsername(refreshTokenVerifyResult.getEmail());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    member.getUsername(),
                    member.getPassword(),
                    member.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
//            setAuthentication(refreshTokenVerifyResult);
            chain.doFilter(request, response);
        } else {
            logger.error(verifyResult.getMessage());
        }
        chain.doFilter(request, response);
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
            if(cookie.getName().equals(REFRESH_COOKIE_NAME))
                return cookie.getValue();

        return "";
    }
}
