package com.example.oauth2jwt.auth.jwt.filter;

import com.example.oauth2jwt.auth.jwt.JwtProvider;
import com.example.oauth2jwt.auth.jwt.response.JwtExceptionResponse;
import com.example.oauth2jwt.global.response.responseItem.ErrorCode;
import com.example.oauth2jwt.utils.HeaderUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {


    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = HeaderUtil.resolveToken(request);
        // 빈 문자열("")**이나 공백만 있는 문자열은 false
        if (!StringUtils.hasText(token)) {
            sendErrorResponse(response, ErrorCode.WRONG_AUTH_HEADER);
            return;
        }
        // JWT에서 토큰을 이용해 인증 정보를 추출 후 UsernamePasswordAuthenticationToken을 생성해 전달
        // Authentication 객체를 생성하고, 이를 SecurityContext에 설정하여 이후의 요청에서 인증 정보를 사용할 수 있도록 힘
        try {
            jwtProvider.validateAccessToken(token);
            Authentication authentication = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
        } catch (Exception e) {
            sendErrorResponse(response, ErrorCode.TOKEN_ERROR);
        }
    }



    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String[] excludePath = {
                "/swagger/**","/swagger-ui/**","/v3/api-docs/**",
                "/auth/**"};//"/v3/api-docs/**" 추가해줘야 swagger 작동
        String path = request.getRequestURI();
        System.out.println("Request URI: " + path);  // URI 로그 출력
        return Arrays.stream(excludePath)
                .anyMatch(exclude -> pathMatcher.match(exclude, path));
    }

    private void sendErrorResponse(HttpServletResponse response,ErrorCode errorCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        JwtExceptionResponse responseJson = new JwtExceptionResponse(
                HttpStatus.valueOf(HttpServletResponse.SC_UNAUTHORIZED), // 상태 메시지
                errorCode.getHttpStatus(),  // 커스텀 상태코드
                errorCode.getMessage(), // 커스텀 메세지
                LocalDateTime.now().toString() // 오류 발생 시각
        );

        String jsonToString = objectMapper.writeValueAsString(responseJson);
        response.getWriter().write(jsonToString);
    }
}


