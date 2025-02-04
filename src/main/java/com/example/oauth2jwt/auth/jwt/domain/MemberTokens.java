package com.example.oauth2jwt.auth.jwt.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberTokens {

    private final String refreshToken;
    private final String accessToken;
}