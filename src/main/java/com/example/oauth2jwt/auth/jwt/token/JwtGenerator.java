package com.example.oauth2jwt.auth.jwt.token;

import static com.example.oauth2jwt.auth.jwt.domain.TokenType.ACCESS;
import static com.example.oauth2jwt.auth.jwt.domain.TokenType.REFRESH;

import com.example.oauth2jwt.auth.jwt.domain.RefreshToken;
import com.example.oauth2jwt.auth.jwt.domain.TokenType;
import com.example.oauth2jwt.auth.jwt.repository.RefreshTokenRepository;
import com.example.oauth2jwt.member.domain.RoleType;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
public class JwtGenerator {

    private static final String CATEGORY_KEY = "category";
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    public JwtGenerator(
        RefreshTokenRepository refreshTokenRepository,
        JwtProperties jwtProperties
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
    }
    // 안정성을 위해 의존성 주입 후 초기화. 생성자에서 초기화하면 의존성 주입이 완전히 안된 상태일수도 있음.
    // 하지만 현재는 생성자에서 초기화해도 정상동작함.
    @PostConstruct
    public void init() {
        this.secretKey = jwtProperties.getSecretKey();
    }

    public String createAccessToken(
        Long memberId,
        RoleType role
    ) {
        Date date = new Date();
        // 임시로 5초 설정, 기존엔 jwtProperties.getAccessTokenExpireTime() 사용
        Date validity = new Date(date.getTime() + 5000L);
        return Jwts.builder()
            .subject(String.valueOf(memberId))
            .claim(CATEGORY_KEY, ACCESS.getValue())
            .claim(TokenType.AUTHORIZATION_HEADER.getValue(), role)
            .expiration(validity)
            .signWith(secretKey)
            .compact();
    }

    public String createRefreshToken() {
        Date date = new Date();
        Date validity = new Date(date.getTime() + jwtProperties.getRefreshTokenExpireTime());
        return Jwts.builder()
            .claim(CATEGORY_KEY, REFRESH.getValue())
            .expiration(validity)
            .signWith(secretKey)
            .compact();
    }

    public void saveRefreshToken(
        String refreshToken,
        Long memberId,
        RoleType role
    ) {
        RefreshToken newRefreshToken = RefreshToken.SaveBuilder()
            .refreshToken(refreshToken)
            .memberId(memberId)
            .role(role)
            .build();
        refreshTokenRepository.save(newRefreshToken);
    }

    public RefreshToken getRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new JwtException("존재하지 않은 토큰입니다."));
    }

}