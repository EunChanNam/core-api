package com.learncha.api.common.security.jwt.model;

import com.learncha.api.auth.domain.Member.AuthType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JWTManager {

    private static final Integer ACCESS_TOKEN_EXPIRATION_TIME = 60 * 15;
    private static final Integer REFRESH_TOKEN_EXPIRATION_TIME = 60 * 60 * 24 * 14;
    private final Key secretKey;

    public JWTManager(@Value("${jwt.secret.key}") String secretValue) {
        log.info("secret value: {}", secretValue);
        if(StringUtils.isBlank(secretValue)) {
            log.debug("Jwt Secret Value Of JWT Manager is NULL");
            throw new RuntimeException("Jwt Secret Value is NULL");
        }
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretValue));
    }

    public TokenVerifyResult verifyToken(String token) {
        try {
            Claims data = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
            return TokenVerifyResult.ofSuccess(data);
        } catch(
            SignatureException |
            IllegalArgumentException |
            MalformedJwtException |
            UnsupportedJwtException e
        ) {
            log.error(Arrays.toString(e.getStackTrace()));
            return TokenVerifyResult.ofErrorToken(e.getMessage());
        }
    }

    public JwtTokenBox generateTokenBox(UserDetailsImpl user) {
        String token = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);
        return JwtTokenBox.of(token, refreshToken, user.getMember().getAuthType().getDescription(), ACCESS_TOKEN_EXPIRATION_TIME);
    }

    public JwtTokenBox generateTokenBoxFromGoogleAuth(String email) {
        String token = generateAccessToken(email);
        String refreshToken = generateRefreshToken(email);
        return JwtTokenBox.of(token, refreshToken, AuthType.GOOGLE.getDescription(), ACCESS_TOKEN_EXPIRATION_TIME);
    }

    public String generateAccessToken(UserDetailsImpl userDetails) {
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setExpiration(new Date(System.currentTimeMillis() + (ACCESS_TOKEN_EXPIRATION_TIME * 1000)))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public String generateAccessToken(String email) {
        return Jwts.builder()
            .setSubject(email)
            .setExpiration(new Date(System.currentTimeMillis() + (ACCESS_TOKEN_EXPIRATION_TIME * 1000)))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    private String generateRefreshToken(UserDetailsImpl userDetails) {
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setExpiration(new Date(System.currentTimeMillis() + (REFRESH_TOKEN_EXPIRATION_TIME * 1000)))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    private String generateRefreshToken(String email) {
        return Jwts.builder()
            .setSubject(email)
            .setExpiration(new Date(System.currentTimeMillis() + (REFRESH_TOKEN_EXPIRATION_TIME * 1000)))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    @Getter
    public static class JwtTokenBox {
        private final String accessToken;
        private final String refreshToken;
        private final String expiredAt;
        private final String authType;

        private JwtTokenBox(
            String accessToken,
            String refreshToken,
            String authType,
            int expiredAt
        ) {
            if(StringUtils.isBlank(accessToken)) throw new IllegalArgumentException("AccessToken is blank");
            if(StringUtils.isBlank(refreshToken)) throw new IllegalArgumentException("RefreshToken is blank");
            if(StringUtils.isBlank(authType)) throw new IllegalArgumentException("Auth Type is blank");
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.authType = authType;
            this.expiredAt = expiredAt + "s";
        }

        public static JwtTokenBox of(String accessToken, String refreshToken, String authType, int expireTime) {
            return new JwtTokenBox(accessToken, refreshToken, authType, expireTime);
        }
    }

    @Getter
    public static class TokenVerifyResult {
        public static final String TOKEN_VALID_MESSAGE = "Token Valid";
        public static final String TOKEN_EXPIRED_MESSAGE = "Token Expired";

        private final boolean result;
        private final String email;
        private final String message;

        @Builder
        public TokenVerifyResult(boolean result, String username, String message) {
            this.result = result;
            this.email = username;
            this.message = message;
        }

        public static TokenVerifyResult ofSuccess(Claims data) {
            return TokenVerifyResult.builder()
                .result(true)
                .username(data.getSubject())
                .message(TOKEN_VALID_MESSAGE)
                .build();
        }

        public static TokenVerifyResult ofTokenExpired() {
            return TokenVerifyResult.builder()
                .result(false)
                .username(null)
                .message(TOKEN_EXPIRED_MESSAGE)
                .build();
        }

        public static TokenVerifyResult ofErrorToken(String errorMessage) {
            return TokenVerifyResult.builder()
                .result(false)
                .username(null)
                .message(errorMessage)
                .build();
        }

        public boolean isVerified() {
            return result;
        }
    }
}
