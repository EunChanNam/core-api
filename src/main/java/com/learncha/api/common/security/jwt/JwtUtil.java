package com.learncha.api.common.security.jwt;

import com.learncha.api.common.security.jwt.model.JwtTokenBox;
import com.learncha.api.common.security.jwt.model.TokenVerifyResult;
import com.learncha.api.common.security.jwt.model.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtUtil {
    private static String SECRET_VALUE = "aaaaaabbbbbbbbcccccccddddddddeeeeeeefffffgggg";
    private static final Key secretKey =  Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_VALUE));
    private static final Integer ACCESS_TOKEN_EXPIRATION_TIME = 60 * 15;
    private static final Integer REFRESH_TOKEN_EXPIRATION_TIME = 3600 * 60 * 24 * 14; // 2주

    public static TokenVerifyResult verifyToken(String token) {
        try {
            Claims data = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
            return new TokenVerifyResult(true, data.getSubject(), "Token Valid");
        } catch(ExpiredJwtException ex) {
            return new TokenVerifyResult(false, null, "Token Expired");
        } catch(
            SignatureException |
            IllegalArgumentException |
            MalformedJwtException |
            UnsupportedJwtException e
        ) {
            log.error(Arrays.toString(e.getStackTrace()));
            return new TokenVerifyResult(false, null, e.getMessage());
        }
    }

    public static JwtTokenBox generateTokenInfo(UserDetailsImpl user) {
        String token = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);
        return JwtTokenBox.of(token, refreshToken, ACCESS_TOKEN_EXPIRATION_TIME);
    }

    public static String generateAccessToken(UserDetailsImpl userDetails) {
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public static String generateAccessTokenFromUserEmail(String email) {
        return Jwts.builder()
            .setSubject(email)
            .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    private static String generateRefreshToken(UserDetailsImpl userDetails) {
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }
}
