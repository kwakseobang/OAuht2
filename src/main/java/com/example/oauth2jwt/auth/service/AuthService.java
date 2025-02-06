package com.example.oauth2jwt.auth.service;


import com.example.oauth2jwt.auth.dto.request.SignUpData;
import com.example.oauth2jwt.auth.dto.response.AccessTokenResponseDto;
import com.example.oauth2jwt.auth.infrastructure.MemberAppender;
import com.example.oauth2jwt.auth.jwt.domain.MemberTokens;
import com.example.oauth2jwt.auth.jwt.provider.JwtProvider;
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
    private final JwtProvider jwtProvider;

    public void signUp(SignUpData signUpData) {
        memberAppender.signUp(signUpData.username(), signUpData.password(), signUpData.nickname());
    }

    @Transactional
    public String login(HttpServletResponse response,String username, String password) {
        Member member = memberValidator.validateLogin(username, password);
        MemberTokens memberTokens = jwtProvider.createTokenAndSaveRefreshToken(member.getId(),member.getRoleType().getCode());
        response.addCookie(CookieUtil.createCookie("refresh_token",memberTokens.getRefreshToken()));
        return memberTokens.getAccessToken();
    }
}
