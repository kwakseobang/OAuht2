package com.example.oauth2jwt.auth.jwt.provider;


import com.example.oauth2jwt.auth.jwt.JwtProperties;
import com.example.oauth2jwt.global.exception.AuthenticationException;
import com.example.oauth2jwt.global.response.responseItem.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtParser {
    private static final String AUTHORITIES_KEY = "auth";

    private final JwtProperties jwtProperties;


    public String getSubject(final String token) {
        return parseToken(token)
                .getPayload()
                .getSubject();
    }
    public String getAuthority(final String token) {
        return parseToken(token)
                .getPayload()
                .get(AUTHORITIES_KEY, String.class);
    }
    // token 파싱
    public Jws<Claims> parseToken(String token) {
        try {
            return Jwts.parser().verifyWith(jwtProperties.getSecretKey()).build().parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException(ErrorCode.TOKEN_EXPIRED);
        } catch (Exception e) {
            throw new AuthenticationException(ErrorCode.BAD_REQUEST_TOKEN);
        }
    }
}
