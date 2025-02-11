package com.example.oauth2jwt.auth.service;


import com.example.oauth2jwt.auth.dto.request.SignUpData;
import com.example.oauth2jwt.auth.infrastructure.MemberAppender;
import com.example.oauth2jwt.auth.jwt.domain.RefreshToken;
import com.example.oauth2jwt.auth.jwt.dto.MemberTokens;
import com.example.oauth2jwt.auth.jwt.dto.TokenInfo;
import com.example.oauth2jwt.auth.jwt.token.JwtCleaner;
import com.example.oauth2jwt.auth.jwt.token.JwtProvider;
import com.example.oauth2jwt.auth.jwt.token.JwtValidator;
import com.example.oauth2jwt.global.error.ErrorCode;
import com.example.oauth2jwt.global.exception.AuthenticationException;
import com.example.oauth2jwt.global.exception.CustomException;
import com.example.oauth2jwt.member.domain.Member;
import com.example.oauth2jwt.member.infrastructure.MemberValidator;
import com.example.oauth2jwt.utils.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberAppender memberAppender;
    private final MemberValidator memberValidator;
    private final JwtValidator jwtValidator;
    private final JwtProvider jwtProvider;
    private final JwtCleaner jwtCleaner;

    public void signUp(SignUpData signUpData) {
        memberAppender.signUp(signUpData.username(), signUpData.password(), signUpData.nickname());
    }

    @Transactional
    public String login(HttpServletResponse response, String username, String password) {
        Member member = memberValidator.validateLogin(username, password);
        MemberTokens memberTokens = jwtProvider.createTokensAndSaveRefreshToken(member.getId(),
                member.getRoleType().getCode());
        // 같은 이름이 있다면 기존에 있던 쿠키 덮어짐.
        response.addCookie(CookieUtil.createCookie("refresh_token", memberTokens.refreshToken()));
        return memberTokens.accessToken();
    }

    public String reissue(HttpServletResponse response, String refreshToken) {
        // logic
        //3. 쿠키에서 RT를 꺼낸 후 유효성 검증을 한다.
        //4. RT에서 memberID로 AccessToken 가져와서 유효서 검증(만료되지 않았다면 탈취 간주. AT RT 폐기)
        //5. 만료 시 리프레시 값 가져와서 쿠키에 있는 리프레시 값과 비교. 다르면 탈취 간주. 폐기.
        //6. 같으면 에세스 토큰, 리프레시 토큰 재발급.
        jwtValidator.validateToken(refreshToken);     // refresh token 유효성 검증
        Long memberId = jwtValidator.getMemberIdFromToken(refreshToken);
        String role = jwtValidator.getRoleFromToken(refreshToken);
        RefreshToken refreshTokenDto = jwtProvider.getRefreshTokenInfo(memberId);  // redis 에서 해당 유저 정보 가져오기

        if (jwtValidator.isValidateTokens(refreshTokenDto.getAccessToken())) { // AccessToken 유효성 검증.(만료되지 않았을경우엔 탈취로 간주,또한 다른 오류일 경우 예외 던짐)
            jwtCleaner.deleteAccessTokenAndRefreshToken(memberId);
            throw new AuthenticationException(ErrorCode.TOKEN_ERROR);
        }
        if (!refreshToken.equals(refreshTokenDto.getRefreshToken())) { // redis 안에 있는 RT와 쿠키의 RT가 같다면 재발급. 탈취된 것으로 보지 않음.
            jwtCleaner.deleteAccessTokenAndRefreshToken(memberId);
            throw new CustomException(ErrorCode.TOKEN_ERROR);
        }
        jwtCleaner.deleteAccessTokenAndRefreshToken(memberId);     // refresh token 은 일회용이라 삭제
        MemberTokens memberTokens = jwtProvider.createTokensAndSaveRefreshToken(memberId, role);
        log.info("memberTokens: " + memberTokens.accessToken());// access token & refresh token 재발급
        log.info("memberTokens: " + memberTokens.refreshToken());// access token & refresh token 재발급
        response.addCookie(CookieUtil.createCookie("refresh_token", memberTokens.refreshToken()));           // RefreshToken 쿠키에 저장.
        return memberTokens.accessToken();
    }
}
