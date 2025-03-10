package com.example.oauth2jwt.auth.controller;

import com.example.oauth2jwt.auth.dto.request.LoginRequestDto;
import com.example.oauth2jwt.auth.dto.request.SignUpRequestDto;
import com.example.oauth2jwt.auth.dto.response.AccessTokenResponseDto;
import com.example.oauth2jwt.auth.service.AuthService;
import com.example.oauth2jwt.global.response.ResponseData;
import com.example.oauth2jwt.global.response.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입 [JWT 사용 X]")
    public ResponseEntity<ResponseData> signUp(@RequestBody SignUpRequestDto requestDto) {
        // 해당 Dto 는 정보가 간단해 그냥 requestDto.filed()로 넘겨줘도 되자만  그냥 적용해봄.
        authService.signUp(requestDto.toSignUpData());
        return ResponseData.success(SuccessCode.CREATED_MEMBER);
    }

    @PostMapping("/login") // 소셜 로그인 이외 로컬 회원가입.
    @Operation(summary = "로그인 [JWT 사용 X]")
    public ResponseEntity<ResponseData<AccessTokenResponseDto>> login(
        HttpServletResponse response,
        @RequestBody LoginRequestDto requestDto
    ) {
        String accessToken = authService.login(
            response,
            requestDto.username(),
            requestDto.password()
        );
        return ResponseData.success(
            SuccessCode.LOGIN_SUCCESS,
            new AccessTokenResponseDto(accessToken)
        );
    }

//    @PostMapping("/reissue")
//    public ResponseEntity<ResponseData<AccessTokenResponseDto>> reissue(
//        HttpServletResponse response,
//        @CookieValue("refresh_token") final String refreshTokenRequest
//    ) {
//        log.info(refreshTokenRequest);
//        String accessToken = authService.reissue(response, refreshTokenRequest);
//        return ResponseData.success(
//            SuccessCode.REISSUE_SUCCESS,
//            new AccessTokenResponseDto(accessToken)
//        );
//    }

}