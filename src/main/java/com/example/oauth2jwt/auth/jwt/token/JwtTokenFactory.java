package com.example.oauth2jwt.auth.jwt.provider;

import com.example.oauth2jwt.auth.jwt.domain.RefreshToken;
import com.example.oauth2jwt.auth.jwt.repository.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class JwtTokenFactory {

    private static final String CATEGORY_KEY = "category";
    private static final String AUTHORITIES_KEY = "auth";

    private final RefreshTokenRepository refreshTokenRepository;

    public String createToken(Long memberId, SecretKey key, String role, String category,
            Long expiredMs) {
        Date date = new Date();
        Date validity = new Date(date.getTime() + expiredMs);

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim(CATEGORY_KEY, category)
                .claim(AUTHORITIES_KEY, role)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    public void saveRefreshToken(String token, Long memberId) {
        RefreshToken refreshToken = RefreshToken.RefreshTokenSaveBuilder()
                .token(token)
                .memberId(memberId)
                .build();
        refreshTokenRepository.save(refreshToken);
    }

}
