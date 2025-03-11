package com.example.oauth2jwt.auth.config;

import com.example.oauth2jwt.auth.jwt.filter.JwtFilter;
import com.example.oauth2jwt.auth.jwt.handler.JwtAccessDeniedHandler;
import com.example.oauth2jwt.auth.jwt.handler.JwtAuthenticationEntryPoint;
import com.example.oauth2jwt.member.domain.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtFilter jwtFilter;
    private final String[] adminUrl = {"/admin/**"};
    private final String[] permitAllUrl = {
        "/", "/error", "/auth/**", "/members/username/**", "/members/nickname/**",
        "/swagger/**", "/swagger-ui/**", "/v3/api-docs/**"
    };
    private final String[] hasRoleUrl = {"/foods/**"};

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)  // jwt 를 이용한 Stateless 방식으로 사용하가에 csrf disable
            .formLogin(AbstractHttpConfigurer::disable)    //From 로그인 방식 disable
            .httpBasic(AbstractHttpConfigurer::disable)       //HTTP Basic 인증 방식 disable
            .sessionManagement(session -> session       //세션 설정 : STATELESS - jwt 방식 사용할 경우
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        //경로별 인가 작업
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(permitAllUrl).permitAll()
                .requestMatchers(adminUrl).hasRole(RoleType.ADMIN.name())
                .requestMatchers(hasRoleUrl).hasAnyRole(
                    RoleType.ADMIN.name(),
                    RoleType.MEMBER.name()
                )
                .anyRequest().authenticated()
            );
        // 예외 처리
        http
            .exceptionHandling(handle -> handle
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler));

        return http.build();
    }

}