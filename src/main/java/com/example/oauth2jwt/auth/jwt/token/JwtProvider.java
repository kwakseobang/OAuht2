package com.example.oauth2jwt.auth.jwt.token;

import static com.example.oauth2jwt.auth.jwt.domain.TokenType.AUTHORIZATION_HEADER;

import com.example.oauth2jwt.auth.jwt.domain.RefreshToken;
import com.example.oauth2jwt.auth.jwt.dto.MemberTokens;
import com.example.oauth2jwt.global.response.error.ErrorCode;
import com.example.oauth2jwt.member.domain.RoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;
import java.util.Collection;
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

    private final JwtGenerator jwtGenerator;
    private final JwtCleaner jwtCleaner;
    private SecretKey secretKey;

    public JwtProvider(
        JwtGenerator jwtGenerator,
        JwtCleaner jwtCleaner,
        JwtProperties jwtProperties
    ) {
        this.jwtGenerator = jwtGenerator;
        this.jwtCleaner = jwtCleaner;
        this.secretKey = jwtProperties.getSecretKey(); // TODO: 이거 하나떄문에 의존성 주입..
    }

    @PostConstruct
    public void init() {

    }

    public MemberTokens createTokens(Long memberId, RoleType role) {
        String accessToken = jwtGenerator.createAccessToken(memberId, role);
        String refreshToken = jwtGenerator.createRefreshToken();
        jwtGenerator.saveRefreshToken(refreshToken, memberId, role);

        return new MemberTokens(accessToken, refreshToken);
    }

    public MemberTokens reissueToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new JwtException(ErrorCode.TOKEN_ERROR.getMessage() + refreshToken);
        }
        RefreshToken storedToken = jwtGenerator.getRefreshToken(refreshToken);
        Long memberId = storedToken.getMemberId();
        jwtCleaner.deleteAccessTokenAndRefreshToken(memberId);

        if (!refreshToken.equals(storedToken.getRefreshToken())) {
            throw new JwtException(ErrorCode.TOKEN_ERROR.getMessage() + refreshToken);
        }
        RoleType role = storedToken.getRole();
        String newAccessToken = jwtGenerator.createAccessToken(memberId, role);
        String newRefreshToken = jwtGenerator.createRefreshToken();
        jwtGenerator.saveRefreshToken(newRefreshToken, memberId, role);

        return new MemberTokens(newAccessToken, newRefreshToken);
    }

    // JWT에서 토큰을 이용해 인증 정보를 추출 후 UsernamePasswordAuthenticationToken을 생성해 전달
    // Authentication 객체를 생성하고, 이를 SecurityContext에 설정하여 이후의 요청에서 인증 정보를 사용할 수 있도록 함.
    public Authentication getAuthentication(String token) {
        String memberId = getClaimsFromToken(token).getSubject();
        // 유저 권한은 하나밖에 없기에 singletonList 로 진행함. 단 하나의 권한만 가질때 사용.
        GrantedAuthority authority = new SimpleGrantedAuthority(getAuthority(token).getValue());

        return new UsernamePasswordAuthenticationToken(
            memberId,
            null,
            Collections.singletonList(authority)
        );
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            validateExpiredToken(claims);
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
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private RoleType getAuthority(String token) {
        return getClaimsFromToken(token)
            .get(AUTHORIZATION_HEADER.getValue(), RoleType.class);
    }

    private void validateExpiredToken(Claims claims) {
        Date expiration = claims.getExpiration();
        if (expiration.before(new Date())) {
            throw new ExpiredJwtException(null, claims, ErrorCode.TOKEN_EXPIRED.getMessage());
        }
    }

    private boolean isExpiredAccessToken(String accessToken) {
        try {
            Claims claims = getClaimsFromToken(accessToken);
            validateExpiredToken(claims);
            log.info("만료되지 않았을 경우 탈취 토큰 폐기");
            return false;
        } catch (ExpiredJwtException e) {
            log.info("만료되었을 경우 토큰 재발급");
            return true;
        }
    }

}