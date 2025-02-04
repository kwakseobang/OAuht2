package com.example.oauth2jwt.utils;

import jakarta.servlet.http.HttpServletRequest;

public class HeaderUtil {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    // 요청 헤더에 Authorization를 보면 접두사 Bearer가 포함되어있음. 제외하고 실제 Access 토큰을 가져오는 함수
    public static String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);
        if(bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(7);  // 접두사 "Bearer "을 제외하고 실제 토큰 문자열을 반환.
        }
        return null;
    }
}
