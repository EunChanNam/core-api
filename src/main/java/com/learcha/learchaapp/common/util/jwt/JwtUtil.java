package com.learcha.learchaapp.common.util.jwt;

import com.learcha.learchaapp.common.util.jwt.model.JwtTokenBox;
import com.learcha.learchaapp.common.util.jwt.model.TokenVerifyResult;
import com.learcha.learchaapp.common.util.jwt.model.UserDetailsImpl;
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
//    @Value("${jwt.secret}")
    private static String SECRET_VALUE = "aaaaaabbbbbbbbcccccccddddddddeeeeeeefffffgggg";
    private static final Key secretKey =  Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_VALUE));
    private static final Integer ACCESS_TOKEN_EXPIRATION_TIME = 60 * 15;
    private static final Integer REFRESH_TOKEN_EXPIRATION_TIME = 3600 * 24 * 14;

    public static String generateAccessToken(UserDetailsImpl userDetails) {
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public static TokenVerifyResult verifyToken(String token) {
        try {
            var data = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
            return new TokenVerifyResult(true, data.getSubject(), "Token is valid");
        } catch(ExpiredJwtException ex) {
            return new TokenVerifyResult(false, null, "Token is expired");
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

    private static String generateRefreshToken(UserDetailsImpl userDetails) {
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }
}
