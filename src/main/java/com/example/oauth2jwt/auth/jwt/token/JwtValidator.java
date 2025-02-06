package com.example.oauth2jwt.auth.jwt.token;

import com.example.oauth2jwt.auth.jwt.dto.TokenInfo;
import com.example.oauth2jwt.auth.jwt.repository.RefreshTokenRepository;
import com.example.oauth2jwt.global.error.ErrorCode;
import com.example.oauth2jwt.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtValidator {
    private final JwtParser jwtParser;
    private final RefreshTokenRepository refreshTokenRepository;

    public void validateAndCheckRefreshTokenInDb(String refreshToken) {
        // 토큰 유효성 검사
        validateToken(refreshToken);
        // 토큰 존재 여부 검사
        if (!isExistRefreshToken(refreshToken)) {
            throw new CustomException(ErrorCode.BAD_REQUEST_TOKEN);
        }
    }
    public void validateToken(String token) {
        jwtParser.parseToken(token);
    }
    public boolean isExistRefreshToken(String token) {
        return refreshTokenRepository.existsById(token);
    }


    public TokenInfo getMemberInfoFromToken(String token) {
        return new TokenInfo(getMemberIdFromToken(token),getRoleFromToken(token));
    }
    private Long getMemberIdFromToken(String token){
        return Long.parseLong(jwtParser.getSubject(token));
    }

    private String getRoleFromToken(String token) {
        return jwtParser.getAuthority(token);
    }

}
