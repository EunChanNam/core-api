package com.learncha.api.common.security.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learncha.api.common.error.ErrorResponse;
import com.learncha.api.common.exception.EntityNotFoundException;
import com.learncha.api.common.security.jwt.model.JWTManager;
import com.learncha.api.common.security.jwt.model.JWTManager.TokenVerifyResult;
import io.jsonwebtoken.ExpiredJwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService customUserDetailService;
    private final ObjectMapper objectMapper;
    private final JWTManager jwtManager;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (bearer == null || !bearer.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = bearer.substring("Bearer ".length());

        try {
            TokenVerifyResult verifyResult = jwtManager.verifyToken(token);
            if(verifyResult.isVerified()) {
                setAuthentication(verifyResult);
            }
            chain.doFilter(request, response);
        } catch(ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            ErrorResponse res = ErrorResponse.of(HttpStatus.UNAUTHORIZED, "Access Token Expired");
            objectMapper.writeValue(response.getOutputStream(), res);
        } catch(EntityNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            ErrorResponse res = ErrorResponse.of(HttpStatus.BAD_REQUEST, "등록된 이메일이 아닙니다.");
            objectMapper.writeValue(response.getOutputStream(), res);
        }
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
}
