package com.example.oauth2jwt.auth.jwt.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public record MemberTokens(String accessToken, String refreshToken) {

}