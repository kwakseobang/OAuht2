package com.example.oauth2jwt.global.config;

import com.example.oauth2jwt.auth.jwt.filter.JwtFilter;
import com.example.oauth2jwt.auth.jwt.handler.JwtAccessDeniedHandler;
import com.example.oauth2jwt.auth.jwt.handler.JwtAuthenticationEntryPoint;
import com.example.oauth2jwt.auth.oauth2.handler.CustomOauth2FailureHandler;
import com.example.oauth2jwt.auth.oauth2.handler.CustomOauth2SuccessHandler;
import com.example.oauth2jwt.auth.oauth2.service.CustomOauth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final CustomOauth2UserService customOAuth2UserService;
    private final CustomOauth2SuccessHandler customSuccessHandler;
    private final CustomOauth2FailureHandler customOauth2FailureHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtFilter jwtFilter;
    private final String[] adminUrl = {"/admin/**"};
    private final String[] permitAllUrl = {
            "/","/error","/auth/**","/members/username/**","/members/nickname/**",
            "/swagger/**","/swagger-ui/**","/v3/api-docs/**"
    };
    private final String[] hasRoleUrl = {"/foods/**"};
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        http
                .csrf((auth) -> auth.disable())  // jwt를 이용한 Stateless 방식으로 사용하가에 csrf disable
                .formLogin((auth) -> auth.disable())    //From 로그인 방식 disable
                .httpBasic((auth) -> auth.disable())       //HTTP Basic 인증 방식 disable
                .sessionManagement((session) -> session       //세션 설정 : STATELESS - jwt 방식 사용할 경우
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig //  **사용자 정보(UserInfo)**를 가져오는 엔드포인트를 설정하는 부분
                                .userService(customOAuth2UserService)) // 인증된 사용자의 정보를 가져와 처리할 서비스
                        .successHandler(customSuccessHandler) // 로그인 성공 시 실행 핸들러
                        .failureHandler(customOauth2FailureHandler) // 로그인 실패 시 실행 핸들러
                );  //oauth2

        http
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(permitAllUrl).permitAll() //모두 허용
                        .requestMatchers(adminUrl).hasRole("ADMIN") //
                        .requestMatchers(hasRoleUrl).hasAnyRole("ADMIN", "MEMBER")
                        .anyRequest().authenticated());
        // 예외 처리
        http
                .exceptionHandling(handle -> handle
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler));



        // 웹 브라우저는 보안 상의 이유로 동일 출처(프로토콜,호스트,포트가 == SOP)에서만 리소스를 공유할 수 있다.
        // cors(Cross-Origin Resource Sharing, 교차 출처 리소스 공유)를 설정해줌으로써 서로 다른 출처 간의 리소스를 공유할 수 있게 한다.
        // 즉 웹 브라우저는 SOP 보안 정책을 기본으로 두면 Cors 허용 시에만 서로 다른 출처끼리 리소스 공유가 가능하다.
        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); // 허용할 origin
                        configuration.setAllowedMethods(Collections.singletonList("*")); // 모든 HTTP 메소드 허용
                        configuration.setAllowCredentials(true); // 인증 정보 포함 허용
                        configuration.setAllowedHeaders(Collections.singletonList("*")); // 모든 헤더 허용
                        configuration.setMaxAge(3600L); // 1시간 동안 캐시 유지.
                        // 클라이언트가 접근할 수 있도록 허용할 응답 헤더
                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                }));

        return http.build();
    }
}

