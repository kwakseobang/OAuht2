package com.example.oauth2jwt.auth.oauth2.handler;

import com.example.oauth2jwt.auth.jwt.token.JwtProvider;
import com.example.oauth2jwt.auth.jwt.dto.MemberTokens;
import com.example.oauth2jwt.auth.oauth2.domain.SocialType;
import com.example.oauth2jwt.auth.oauth2.dto.CustomOauth2User;
import com.example.oauth2jwt.utils.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/* !!! 버그 발생 !!! -> 기존에 Refresh토큰은 쿠키 AccessToken은 헤더에 담은 후 리다이렉팅 했음.
    - 이 과정에서 리다이렉팅 할 경우 헤더에 담은 AccessToken이 소실 됨. why?
    - 백엔드가 sendRedirect(url)를 호출하면 클라이언트(브라우저)에게 302 응답을 보냅니다.
    - 리다이렉팅은 브라우저를 통해 다른 페이지르 이동하라는 건데. 그 과정에서 새로운 요청과 응답이 생기기에 헤더가 리셋됨.(http는 stateless)
    - 따라서 헤더에 담아서 보내주면 안됨

    ------------- 수정 방안  --------------
    한줄 요약. 임시 토큰을 생성해서 redirect Url 쿼리 파라미터에 보낸 후 임시 토큰으로 다시 서버에 에세스 토큰 요청. refresh토큰은 기존대로 쿠키에 저장

*/

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomOauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private static final String REDIRECT_URL = "http://localhost:3000/";

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

        MemberTokens memberTokens = jwtProvider.createTokensAndSaveRefreshToken(memberId, role);

        String accessToken = memberTokens.accessToken();
        String temporaryTokenKey = jwtProvider.getTemporaryTokenKey(accessToken);
        String refreshToken = memberTokens.refreshToken();
        addRefreshTokenCookie(response, refreshToken);

        String redirectUrl =  redirectToPageByRole(String.valueOf(oAuth2User.getAuthorities()),temporaryTokenKey,oAuth2User.getName());

        redirectToFrontend(request, response, redirectUrl);

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

        response.addCookie(CookieUtil.createCookie("refresh_token", refreshToken));
    }

    private void redirectToFrontend(HttpServletRequest request, HttpServletResponse response,
            String redirectUrl) throws IOException {
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private String createRedirectUrl(String frontendPath, String tempTokenKey, String nickname) {
        String frontendUrl = REDIRECT_URL + frontendPath;

        String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl)  // 프론트엔드 url
                .queryParam("temp-token", tempTokenKey)
                .queryParam("nickname", nickname)
                .build().toUriString();
        return redirectUrl;
    }
    private String redirectToPageByRole(String role, String tempTokenKey, String nickname) {
        if (role.equals("MEMBER")) {
            log.info(
                    "기존 회원 입니다. 임시 토큰으로 accessToken 발급 요청 후 메인 페이지로 리다이렉팅 됩니다.");
            return createRedirectUrl("/access-token", tempTokenKey, nickname);
        } else {
            log.info("신규 회원 입니다. 추가정보 입력을 위한 회원가입 페이지로 리다이렉팅 됩니다 입력 후 임시 토큰으로 accessToken 발급 요청 후 메인 페이지로 리다이렉팅 됩니다.");
            return createRedirectUrl("/signup", tempTokenKey, nickname);
        }
    }


}
