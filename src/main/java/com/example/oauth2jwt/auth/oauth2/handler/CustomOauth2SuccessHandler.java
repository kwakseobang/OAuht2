package com.example.oauth2jwt.auth.oauth2.handler;

import com.example.oauth2jwt.auth.jwt.provider.JwtProvider;
import com.example.oauth2jwt.auth.jwt.domain.MemberTokens;
import com.example.oauth2jwt.auth.jwt.domain.RefreshToken;
import com.example.oauth2jwt.auth.jwt.repository.RefreshTokenRepository;
import com.example.oauth2jwt.auth.oauth2.dto.CustomOauth2User;
import com.example.oauth2jwt.utils.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
@Slf4j
@RequiredArgsConstructor
@Component
public class CustomOauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;
    private static final String REDIRECT_URL = "http://localhost:3000/";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        //OAuth2User
        CustomOauth2User oAuth2User = (CustomOauth2User) authentication.getPrincipal();
        Long memberId = oAuth2User.getId();
        String role = getRoleFromAuthentication(authentication);

        MemberTokens memberTokens = jwtProvider.createTokenAndSaveRefreshToken(memberId,role);

        String accessToken =  memberTokens.getAccessToken();
        String refreshToken = memberTokens.getRefreshToken();
        addRefreshTokenCookie(response,refreshToken);

        // 액세스 토큰을 응답 헤더에 추가
        addAuthorizationHeader(response,accessToken);
        // front로 redirect
        redirectToFrontend(request,response);

        // 여기서 발생한 오류를 어떻게 예외 처리 해야될지모르겠음
    }

    // =================== 유틸성 메소드 ===================

    private String getRoleFromAuthentication(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new AuthenticationException("No authorities found") {
                });
    }

    // 리프레시 토큰을 HttpOnly 쿠키에 저장하는 메서드
    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {

        response.addCookie(CookieUtil.createCookie("refresh_token",refreshToken));
    }
    private void addAuthorizationHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(AUTHORIZATION_HEADER, BEARER + accessToken);
    }

    private void redirectToFrontend(HttpServletRequest request, HttpServletResponse response) throws IOException {
        getRedirectStrategy().sendRedirect(request, response, REDIRECT_URL);
    }

}
