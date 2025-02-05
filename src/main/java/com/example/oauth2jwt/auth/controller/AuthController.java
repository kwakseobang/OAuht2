package com.example.oauth2jwt.auth.controller;

import com.example.oauth2jwt.auth.dto.request.SignUpRequestDto;
import com.example.oauth2jwt.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private AuthService loginService;
    @GetMapping("/signup") // 소셜 로그인 이외 로컬 회원가입.
    public ResponseEntity<Void> signUp(@RequestBody SignUpRequestDto requestDto) {

        loginService.signUp(requestDto.toSignUpData());
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/login") // 소셜 로그인 이외 로컬 회원가입.
//    public ResponseEntity<ResponseData<AccessTokenResponseDto>> login() {
//    return ResponseData.toResponseEntity(ResponseCode.LOGIN_SUCCESS);
//    }


//    @PostMapping("/reissue")

}
