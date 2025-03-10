package com.example.oauth2jwt.auth.jwt.token;

import static com.example.oauth2jwt.auth.jwt.domain.TokenType.ACCESS;
import static com.example.oauth2jwt.auth.jwt.domain.TokenType.REFRESH;

import com.example.oauth2jwt.auth.jwt.domain.RefreshToken;
import com.example.oauth2jwt.auth.jwt.domain.TokenType;
import com.example.oauth2jwt.auth.jwt.repository.RefreshTokenRepository;
import com.example.oauth2jwt.member.domain.RoleType;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class JwtTokenFactory {

    private static final String CATEGORY_KEY = "category";
    private final RefreshTokenRepository refreshTokenRepository;

    public String createAccessToken(
        Long memberId,
        RoleType role,
        SecretKey secretKey,
        long expiredMs
    ) {
        Date date = new Date();
        Date validity = new Date(date.getTime() + expiredMs);
        return Jwts.builder()
            .subject(String.valueOf(memberId))
            .claim(CATEGORY_KEY, ACCESS.getValue())
            .claim(TokenType.AUTHORIZATION_HEADER.getValue(), role)
            .expiration(validity)
            .signWith(secretKey)
            .compact();
    }

    public String createRefreshToken(SecretKey secretKey, long expiredMs) {
        Date date = new Date();
        Date validity = new Date(date.getTime() + expiredMs);
        return Jwts.builder()
            .claim(CATEGORY_KEY, REFRESH.getValue())
            .expiration(validity)
            .signWith(secretKey)
            .compact();
    }

    public void saveRefreshToken(String refreshToken, Long memberId) {
        RefreshToken newRefreshToken = RefreshToken.SaveBuilder()
            .refreshToken(refreshToken)
            .memberId(memberId)
            .build();
        refreshTokenRepository.save(newRefreshToken);
    }

    public void deleteAccessTokenAndRefreshToken(Long memberId) {
        refreshTokenRepository.deleteById(memberId);
    }

}