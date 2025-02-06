package com.example.oauth2jwt.auth.service;


import com.example.oauth2jwt.auth.dto.request.SignUpData;
import com.example.oauth2jwt.auth.infrastructure.MemberAppender;
import com.example.oauth2jwt.auth.jwt.dto.MemberTokens;
import com.example.oauth2jwt.auth.jwt.dto.TokenInfo;
import com.example.oauth2jwt.auth.jwt.token.JwtCleaner;
import com.example.oauth2jwt.auth.jwt.token.JwtProvider;
import com.example.oauth2jwt.auth.jwt.token.JwtValidator;
import com.example.oauth2jwt.global.error.ErrorCode;
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
        // refresh token 유효성 검증
        jwtValidator.validateAndCheckRefreshTokenInDb(refreshToken);

        // 토큰에서 member 정보 가져오기
        TokenInfo tokenInfo = jwtValidator.getMemberInfoFromToken(refreshToken);
        // refresh token은 일회용이라 삭제
        jwtCleaner.deleteRefreshToken(refreshToken);
        // access token & refresh token 재발급
        MemberTokens memberTokens = jwtProvider.createTokensAndSaveRefreshToken(tokenInfo.memberId(), tokenInfo.Role());
        // RefreshToken 쿠키에 저장.
        response.addCookie(CookieUtil.createCookie("refresh_token", memberTokens.refreshToken()));

        return memberTokens.accessToken();
    }
}
