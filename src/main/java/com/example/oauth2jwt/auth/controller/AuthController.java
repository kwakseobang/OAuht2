package com.example.oauth2jwt.auth.controller;

import com.example.oauth2jwt.auth.dto.request.LoginRequestDto;
import com.example.oauth2jwt.auth.dto.request.SignUpRequestDto;
import com.example.oauth2jwt.auth.dto.response.AccessTokenResponseDto;
import com.example.oauth2jwt.auth.jwt.token.JwtProvider;
import com.example.oauth2jwt.auth.service.AuthService;
import com.example.oauth2jwt.global.response.ResponseData;
import com.example.oauth2jwt.global.response.responseItem.SuccessCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup") // 소셜 로그인 이외 로컬 회원가입.
    public ResponseEntity<ResponseData> signUp(@RequestBody SignUpRequestDto requestDto) {

        // 해당 Dto는 정보가 간단해 그냥 requstDto.filed()로 넘겨줘도 되자만  그냥 적용해봄.
        authService.signUp(requestDto.toSignUpData());
        return ResponseData.success(SuccessCode.CREATED_MEMBER);
    }

    @PostMapping("/login") // 소셜 로그인 이외 로컬 회원가입.
    public ResponseEntity<ResponseData<AccessTokenResponseDto>> login(
            HttpServletResponse response,
            @RequestBody LoginRequestDto requestDto
    ) {
        String accessToken = authService.login(response, requestDto.username(), requestDto.password());
        return ResponseData.success(SuccessCode.LOGIN_SUCCESS,
                new AccessTokenResponseDto(accessToken));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ResponseData<AccessTokenResponseDto>> reissue(
            HttpServletResponse response,
            @CookieValue("refresh_token") final String refreshTokenRequest
    ) {
        log.info(refreshTokenRequest);
        String accessToken = authService.reissue(response, refreshTokenRequest);
        return ResponseData.success(SuccessCode.REISSUE_SUCCESS,
                new AccessTokenResponseDto(accessToken));
    }

    @PostMapping("/access-token")
    public ResponseEntity<ResponseData<AccessTokenResponseDto>> getAccessToken(@RequestParam("temp-token") String tempToken) {

        String accessToken = authService.getAccessTokenFromTempToken(tempToken);
        return ResponseData.success(SuccessCode.ACCESS_TOKEN_SUCCESS,
                new AccessTokenResponseDto(accessToken));
    }

}
