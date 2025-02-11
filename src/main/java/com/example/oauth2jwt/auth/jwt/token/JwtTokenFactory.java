package com.example.oauth2jwt.auth.jwt.token;

import com.example.oauth2jwt.auth.jwt.domain.RefreshToken;
import com.example.oauth2jwt.auth.jwt.domain.TemporaryToken;
import com.example.oauth2jwt.auth.jwt.repository.RefreshTokenRepository;
import com.example.oauth2jwt.auth.jwt.repository.TemporaryTokenRepository;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Component
public class JwtTokenFactory {
    private static final String CATEGORY_KEY = "category";
    private static final String AUTHORITIES_KEY = "auth";

    private final RefreshTokenRepository refreshTokenRepository;
    private final TemporaryTokenRepository temporaryTokenRepository;

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

    public void saveRefreshToken(String refreshToken, String accessToken, Long memberId) {
        RefreshToken newRefreshToken = RefreshToken.SaveBuilder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .memberId(memberId)
                .build();
        refreshTokenRepository.save(newRefreshToken);
    }

    public void saveTemporaryToken(String tempToken, String accessToken) {

        TemporaryToken temporaryToken = TemporaryToken.builder()
                .temporaryToken(tempToken)
                .accessToken(accessToken)
                .build();
        temporaryTokenRepository.save(temporaryToken);
    }
}
