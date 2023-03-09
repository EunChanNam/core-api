package com.learncha.api.common.security.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learncha.api.auth.domain.Member.AuthType;
import com.learncha.api.auth.web.AuthDto.LoginSuccessResponse;
import com.learncha.api.common.security.jwt.model.JWTManager;
import com.learncha.api.common.security.jwt.model.JWTManager.JwtTokenBox;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomOAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JWTManager jwtManager;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {
        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = defaultOAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        var name = (String) defaultOAuth2User.getAttributes().get("name");
        JwtTokenBox tokenBox = jwtManager.generateTokenBoxFromGoogleAuth(email);

        LoginSuccessResponse res = LoginSuccessResponse.builder()
            .email(email)
            .name(name)
            .authType(AuthType.GOOGLE.getDescription())
            .accessToken(tokenBox.getAccessToken())
            .build();

        Cookie cookie = new Cookie("refresh_token", tokenBox.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        objectMapper.writeValue(response.getOutputStream(), res);
    }
}
