package com.example.oauth2jwt.auth.service;


import static com.example.oauth2jwt.auth.jwt.domain.TokenType.REFRESH;

import com.example.oauth2jwt.auth.dto.request.SignUpData;
import com.example.oauth2jwt.auth.jwt.dto.MemberTokens;
import com.example.oauth2jwt.auth.jwt.token.JwtProvider;
import com.example.oauth2jwt.member.domain.Member;
import com.example.oauth2jwt.member.infrastructure.MemberAppender;
import com.example.oauth2jwt.member.infrastructure.MemberReader;
import jakarta.servlet.http.Cookie;
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
    private final MemberReader memberReader;
    private final JwtProvider jwtProvider;

    public void signUp(SignUpData signUpData) {
        memberAppender.signUp(signUpData.username(), signUpData.password(), signUpData.nickname());
    }

    @Transactional
    public String login(HttpServletResponse response, String username, String password) {
        Member member = memberReader.login(username, password);
        MemberTokens memberTokens = jwtProvider.createTokens(
            member.getId(),
            member.getRole()
        );
        // 같은 이름이 있다면 기존에 있던 쿠키 덮어짐.
        response.addCookie(createCookie(REFRESH.getValue(), memberTokens.refreshToken()));
        return memberTokens.accessToken();
    }

    public String reissue(
        HttpServletResponse response,
        String refreshToken
    ) {
        // AT 재발급 시 RT도 재발급됨.
        // -> 공격자가 탈취 후 새로 발급 받은 후 사용자가 재발급했을 때 DB RT와 사용자 RT가 다르기에 탈취 간주로 둘 다 폐기.
        MemberTokens memberTokens = jwtProvider.reissueToken(refreshToken);
        // 같은 이름이 있다면 기존에 있던 쿠키 덮어짐.
        response.addCookie(createCookie(REFRESH.getValue(), memberTokens.refreshToken()));
        return memberTokens.accessToken();
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }

}