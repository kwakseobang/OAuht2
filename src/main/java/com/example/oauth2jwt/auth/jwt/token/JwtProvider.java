package com.example.oauth2jwt.auth.jwt.token;

import static com.example.oauth2jwt.auth.jwt.domain.TokenType.AUTHORIZATION_HEADER;

import com.example.oauth2jwt.auth.jwt.dto.MemberTokens;
import com.example.oauth2jwt.global.response.error.ErrorCode;
import com.example.oauth2jwt.member.domain.RoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Collections;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtProvider {

    private final JwtTokenFactory jwtTokenFactory;
    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtProvider(JwtTokenFactory jwtTokenFactory, JwtProperties jwtProperties) {
        this.jwtTokenFactory = jwtTokenFactory;
        this.jwtProperties = jwtProperties;
        this.secretKey = jwtProperties.getSecretKey();
    }

    public MemberTokens createTokens(Long memberId, RoleType roleType) {
        String accessToken = jwtTokenFactory.createAccessToken(
            memberId,
            roleType,
            secretKey,
            jwtProperties.getAccessTokenExpireTime()
        );
        String refreshToken = jwtTokenFactory.createRefreshToken(
            secretKey,
            jwtProperties.getRefreshTokenExpireTime()
        );
        jwtTokenFactory.saveRefreshToken(refreshToken, memberId);

        return new MemberTokens(accessToken, refreshToken);
    }

    // JWT에서 토큰을 이용해 인증 정보를 추출 후 UsernamePasswordAuthenticationToken을 생성해 전달
    // Authentication 객체를 생성하고, 이를 SecurityContext에 설정하여 이후의 요청에서 인증 정보를 사용할 수 있도록 함.
    public Authentication getAuthentication(String token) {
        String memberId = getClaimsFromToken(token).getSubject();
        // 유저 권한은 하나밖에 없기에 singletonList 로 진행함. 단 하나의 권한만 가질때 사용.
        GrantedAuthority authority = new SimpleGrantedAuthority(getAuthority(token));

        return new UsernamePasswordAuthenticationToken(
            memberId,
            null,
            Collections.singletonList(authority)
        );
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            isExpiredToken(claims);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error(ErrorCode.TOKEN_ERROR.getMessage() + token);
        } catch (ExpiredJwtException e) {
            log.error(ErrorCode.TOKEN_EXPIRED.getMessage() + token);
        } catch (UnsupportedJwtException e) {
            log.error(ErrorCode.TOKEN_HASH_NOT_SUPPORTED.getMessage() + token);
        } catch (IllegalArgumentException e) {
            log.error(ErrorCode.BAD_REQUEST_TOKEN.getMessage() + token);
        }
        return false;
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
            .verifyWith(jwtProperties.getSecretKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private void isExpiredToken(Claims claims) {
        Date expiration = claims.getExpiration();
        if (expiration.before(new Date())) {
            throw new ExpiredJwtException(null, claims, ErrorCode.TOKEN_EXPIRED.getMessage() );
        }
    }

    private String getAuthority(final String token) {
        return getClaimsFromToken(token).get(AUTHORIZATION_HEADER.getValue(), String.class);
    }

}