package com.learncha.api.common.security.jwt.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learncha.api.auth.domain.Member.AuthType;
import com.learncha.api.auth.web.AuthDto.LoginSuccessResponse;
import com.learncha.api.common.security.jwt.model.JWTManager;
import com.learncha.api.common.security.jwt.model.JWTManager.JwtTokenBox;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomOAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final String FRONT_END_POINT = "learncha.com";
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

        Cookie cookie = new Cookie("refresh_token", tokenBox.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setDomain(FRONT_END_POINT);
        response.addCookie(cookie);

        URI targetUrl = UriComponentsBuilder.fromUriString("https://learncha.com" + "/auth/welcome")
            .queryParam("access-token", tokenBox.getAccessToken())
            .queryParam("name", name)
            .queryParam("email", email)
            .queryParam("auth-type", AuthType.GOOGLE.getDescription())
            .build()
            .encode()
            .toUri();

        getRedirectStrategy().sendRedirect(request, response, targetUrl.toString());
    }
}
