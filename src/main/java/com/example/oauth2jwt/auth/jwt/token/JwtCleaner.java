package com.example.oauth2jwt.auth.jwt.token;

import com.example.oauth2jwt.auth.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class JwtCleaner {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void deleteAccessTokenAndRefreshToken(Long memberId) {
        refreshTokenRepository.deleteById(memberId);
    }

}