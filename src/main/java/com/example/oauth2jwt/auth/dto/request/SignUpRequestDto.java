package com.example.oauth2jwt.auth.dto.request;

public record SignUpRequestDto(String username, String password, String nickname) {

    public SignUpData toSignUpData() {
        return new SignUpData(username, password, nickname);
    }

}