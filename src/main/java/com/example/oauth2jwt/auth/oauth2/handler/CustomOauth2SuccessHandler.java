package com.example.oauth2jwt.auth.oauth2.handler;

import com.example.oauth2jwt.auth.jwt.JwtProvider;
import com.example.oauth2jwt.auth.jwt.domain.MemberTokens;
import com.example.oauth2jwt.auth.jwt.domain.RefreshToken;
import com.example.oauth2jwt.auth.jwt.repository.RefreshTokenRepository;
import com.example.oauth2jwt.auth.oauth2.dto.CustomOauth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
@Slf4j
@RequiredArgsConstructor
@Component
public class CustomOauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        //OAuth2User
        CustomOauth2User oAuth2User = (CustomOauth2User) authentication.getPrincipal();

        Long memberId = oAuth2User.getId();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        MemberTokens memberTokens = jwtProvider.createTokens(memberId,role);

        String accessToken =  memberTokens.getAccessToken();
        RefreshToken refreshToken = createRefreshToken(memberTokens.getRefreshToken(),memberId);
        refreshTokenRepository.save(refreshToken);
        addRefreshTokenCookie(response,refreshToken.getToken());

        // 액세스 토큰을 응답 헤더에 추가
        response.setHeader("Authorization", "Bearer " + accessToken);
        log.info(oAuth2User.getName());
        log.info(oAuth2User.getUsername());
        log.info(refreshToken.getToken());
        log.info(accessToken);
        getRedirectStrategy().sendRedirect(request,response,getRedirectUrl());
    }

    // =================== 유틸성 메소드 ===================
    private RefreshToken createRefreshToken(String token, long memberId) {
        return RefreshToken.RefreshTokenSaveBuilder()
                .token(token)
                .memberId(memberId)
                .build();
    }
    private String getRedirectUrl() {
        return "http://localhost:3000/";
    }
    // 리프레시 토큰을 HttpOnly 쿠키에 저장하는 메서드
    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {

        response.addCookie( createCookie("refresh_token",refreshToken));
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
