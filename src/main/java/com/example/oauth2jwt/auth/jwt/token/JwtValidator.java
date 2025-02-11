package com.example.oauth2jwt.auth.jwt.token;

import com.example.oauth2jwt.auth.jwt.domain.RefreshToken;
import com.example.oauth2jwt.auth.jwt.dto.TokenInfo;
import com.example.oauth2jwt.auth.jwt.repository.RefreshTokenRepository;
import com.example.oauth2jwt.global.error.ErrorCode;
import com.example.oauth2jwt.global.exception.CustomException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
//  A  R
//  A2 R
//
// DB - A2 R
// 기본 인증 로직
// 재발급 로직.
// R을 보낸다. R로 DB에서 가져온다
//
@Component
@RequiredArgsConstructor
public class JwtValidator {
    private final JwtParser jwtParser;
    private final RefreshTokenRepository refreshTokenRepository;

//    public void validateRefreshToken(String refreshToken) {
//        // Redis에서 refershToken 가져옴.
//        RefreshToken foundTokenInfo = refreshTokenRepository.findByRefreshToken(refreshToken)
//                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_ERROR)); // 토큰 만료되었으면 삭제 되기 떄문에 없을 것이다.
//        String refreshToken = foundTokenInfo.getRefreshToken();
//        validateToken(refreshToken);
//    }
//    public void validateAndCheckRefreshTokenInDb(String refreshToken) {
//        // 토큰 유효성 검사
//        validateToken(refreshToken);
//        // 토큰 존재 여부 검사
//        if (!isExistRefreshToken(refreshToken)) {
//            throw new CustomException(ErrorCode.BAD_REQUEST_TOKEN);
//        }
//    }
    public void validateToken(String token) {
        jwtParser.parseToken(token);
    }

    public boolean isValidateTokens(String token) {
        try {
            jwtParser.parseToken(token);
            return true;
        } catch (ExpiredJwtException e) { // 만료 되었으면 통과 안되었으면 탈취 간주
            return false;
        } catch (Exception e) { // 다른 에러일 경우에도 폐기
            return false;
        }
    }
//    public boolean isExistRefreshToken(String token) {
//        return refreshTokenRepository.existsById(token);
//    }


    public TokenInfo getMemberInfoFromToken(String token) {
        return new TokenInfo(getMemberIdFromToken(token),getRoleFromToken(token));
    }
    public Long getMemberIdFromToken(String token){
        return Long.parseLong(jwtParser.getSubject(token));
    }

    public String getRoleFromToken(String token) {
        return jwtParser.getAuthority(token);
    }

}
