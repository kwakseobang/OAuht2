package com.example.oauth2jwt.auth.jwt.token;

import com.example.oauth2jwt.auth.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtCleaner {

    private final RefreshTokenRepository refreshTokenRepository;

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteById(token);
    }
}
