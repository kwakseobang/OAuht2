package com.example.oauth2jwt.auth.controller;

import com.example.oauth2jwt.global.response.ResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    @GetMapping("/signup") // 소셜 로그인 이외 로컬 회원가입.
    public ResponseEntity<Void> signUp() {

        return ResponseEntity.ok().build();
    }

    @GetMapping("/login") // 소셜 로그인 이외 로컬 회원가입.
    public ResponseEntity<ResponseData<>> login() {
        return "main route";
    }


    @PostMapping("/reissue")

}
