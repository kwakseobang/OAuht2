package com.example.oauth2jwt.auth.jwt.filter;

import com.example.oauth2jwt.auth.jwt.provider.JwtProvider;
import com.example.oauth2jwt.auth.jwt.response.JwtErrorResponder;
import com.example.oauth2jwt.global.error.ErrorCode;
import com.example.oauth2jwt.utils.HeaderUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/*
=============== JWT Logic ===================
0. JWT 기반 인증에서는 UsernamePasswordAuthenticationFilter 대신 JWT 필터를 사용함.
1. 클라이언트가 JWT 토큰을 보내면 해당 필터가 요청을 가로챈다. 가로챈 정보로 UsernamePasswordAuthenticationToken 토큰(인증용 객체)을 생성

2. AuthenticationFilter에서 생성된 UsernamePasswordAuthenticationToken을 AuthenticationManager 에게 전달함. AuthenticationManager 에게 인증 진행하라고 한다.

3. AuthenticationManager는 등록된 AuthenticationProvider(들)을 조회하여 인증을 요구한다.
    - AuthenticationManager는 List 형태로 AuthenticationProvider들을 가지고 있는데, 실제로 인증을 할 AuthenticationProvider에게 인증용 객체(토큰)를 다시 위임한다.

4. 선택된 AuthenticationProvider는 UserDetailsService를 통해 입력받은 아이디에 대한 사용자 정보를 DB에서 조회함
    - UserDetailsService 인터페이스는 loadUserByUsername() 메서드를 호출하여 DB에 있는 사용자 정보를 ** UserDetails ** 형태로 가져온다. 없을 시 예외 던짐.

5. 인증이 완료되면 인증 정보를 담고 있는 Authentication 객체를 생성해서 AuthenticationManager에게 전달
    - AuthenticationManager는 AuthenticationFilter에게 전달

6. Authentication 객체를 SecurityContextHolder라는 곳에 저장 후 AuthenticationSuccessHandler을 실행한다
    - SecurityContextHolder는 현재 실행 중인 스레드(Thread)에 대한 보안 컨텍스트(SecurityContext)를 저장하는 역할을 함.
        - 기본적으로 ThreadLocal을 사용해서 요청마다 다른 인증 정보를 저장함.
    - SecurityContextHolder가 관리하는 SecurityContext에 저장된다.
    - SecurityContext에는 Authentication 객체를 가지고 있다. Authentication는 현재 사용자의 인증 정보를 담고 있음.
    - 스프링 시큐리티는 요청이 끝나면 SecurityContextHolder를 자동으로 초기화함. 즉 요청이 끝나면 저장된 Authentication는 사라짐. why? jwt는 stateless이기 떄문이다.
 */

/*
=============== JWT Logic (DB 조회 생략)===================
-- 토큰 자체가 사용자의 인증 정보를 포함하고 있기 때문에 매번 DB 조회 없이 사용 가능.
1. 클라이언트가 JWT 토큰을 보내면 해당 필터가 요청을 가로챈다.
2. JWT 검증 후 UsernamePasswordAuthenticationToken 토큰(인증용 객체)을 생성
3. Authentication 객체를 SecurityContextHolder라는 곳에 저장
 */
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {


    private final JwtProvider jwtProvider;
    private final JwtErrorResponder jwtErrorResponder;

    private static final List<String> EXCLUDE_PATHS = List.of(
            "/swagger/**", "/swagger-ui/**", "/v3/api-docs/**", "/auth/**"
    );
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
            jwtErrorResponder.sendErrorResponse(response, ErrorCode.WRONG_AUTH_HEADER);
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
            jwtErrorResponder.sendErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
        } catch (Exception e) {
            jwtErrorResponder.sendErrorResponse(response, ErrorCode.TOKEN_ERROR);
        }
    }



    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return EXCLUDE_PATHS.stream()
                .anyMatch(exclude -> pathMatcher.match(exclude, path));
    }

}


