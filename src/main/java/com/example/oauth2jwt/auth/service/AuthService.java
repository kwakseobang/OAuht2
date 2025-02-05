package com.example.oauth2jwt.auth.service;


import com.example.oauth2jwt.auth.dto.request.SignUpData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public void signUp(SignUpData signUpData) {

    }

    public void login() {

    }
}
