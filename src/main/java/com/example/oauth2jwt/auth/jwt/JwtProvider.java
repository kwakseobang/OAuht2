package com.example.oauth2jwt.auth.jwt;

import com.example.oauth2jwt.auth.jwt.domain.MemberTokens;
import com.example.oauth2jwt.global.exception.AuthenticationException;
import com.example.oauth2jwt.global.response.responseItem.ErrorCode;
import com.example.oauth2jwt.member.domain.Member;
import com.example.oauth2jwt.member.domain.RoleType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.management.relation.Role;
import javax.security.auth.login.LoginException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponseException;

// @RequiredArgsConstructor 와 @Value는 같이 사용 못한다.  컴파일 타임에 Lombok과 충돌이 생길 수 있다.
@Component
public class JwtProvider {

    private final Long accessTokenExpireTime;
    private final Long refreshTokenExpireTime;
    private final SecretKey secretKey;
    private static final String MEMBER_ID_KEY = "id";
    private static final String CATEGORY_KEY = "category";
    private static final String AUTHORITIES_KEY = "auth";

    public JwtProvider(
            @Value("${spring.jwt.access.expiration}") Long accessTokenExpireTime,
            @Value("${spring.jwt.refresh.expiration}")  Long refreshTokenExpireTime,
            @Value("${spring.jwt.secretKey}")String secretKey
    ){
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
        this.secretKey =new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }


    public MemberTokens createTokens(Long memberId, String roleType) {
        String accessToken = createToken(memberId,roleType, "access",accessTokenExpireTime);
        String refreshToken = createToken(memberId,roleType, "refresh",refreshTokenExpireTime);

        return new MemberTokens(refreshToken, accessToken);
    }

    private String createToken(Long memberId, String role, String category, Long expiredMs) {
        Date date = new Date();
        Date validity = new Date(date.getTime() + expiredMs);

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim(MEMBER_ID_KEY,memberId)
                .claim(CATEGORY_KEY, category)
                .claim(AUTHORITIES_KEY,role)
                .expiration(validity)
                .signWith(secretKey)
                .compact();
    }


    // JWT에서 토큰을 이용해 인증 정보를 추출 후 UsernamePasswordAuthenticationToken을 생성해 전달
    // Authentication 객체를 생성하고, 이를 SecurityContext에 설정하여 이후의 요청에서 인증 정보를 사용할 수 있도록 함.
    public Authentication getAuthentication(String token) {
        String memberId  = getSubject(token);
        // 유저 권한은 하나밖에 없기에 singletonList로 진행함. 단 하나의 권한만 가질때 사용.
        String auth = getAuthority(token);
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(auth);
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(grantedAuthority);

        return new UsernamePasswordAuthenticationToken(memberId, null, authorities);
    }

    public boolean validateAccessToken(String accessToken) {
        parseToken(accessToken);
        return true;
    }

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
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
           throw new AuthenticationException(ErrorCode.TOKEN_EXPIRED);
        } catch (Exception e) {
           throw new AuthenticationException(ErrorCode.BAD_REQUEST_TOKEN);
        }
    }
}