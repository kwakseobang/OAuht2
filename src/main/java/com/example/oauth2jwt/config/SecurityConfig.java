package com.example.oauth2jwt.config;

import com.example.oauth2jwt.jwt.JwtFilter;
import com.example.oauth2jwt.jwt.JwtUtil;
import com.example.oauth2jwt.oauth2.CustomSuccessHandler;
import com.example.oauth2jwt.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        http
                .csrf((auth) -> auth.disable())  // jwt를 이용한 Stateless 방식으로 사용하가에 csrf disable
                .formLogin((auth) -> auth.disable())    //From 로그인 방식 disable
                .httpBasic((auth) -> auth.disable());       //HTTP Basic 인증 방식 disable
        //oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig //  **사용자 정보(UserInfo)**를 가져오는 엔드포인트를 설정하는 부분
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                );  //oauth2

        http
                .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/").permitAll()
                        .anyRequest().authenticated());

        //세션 설정 : STATELESS
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // port 번호가 달라 데이터를 못가져오는 경우를 방지함.
        // 프론트에서 json으로 넘긴 데이터를 보이지 않음. 그런 문제를 해결하기 위해 서버에서 해당 프론트 port를 허용해야함.
        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);
                        // 프론트단에서도 볼 수 있게 설정.
                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                }));

        return http.build();
    }
}
