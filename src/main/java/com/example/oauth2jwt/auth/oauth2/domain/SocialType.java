package com.example.oauth2jwt.auth.oauth2.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum SocialType {
    GOOGLE,
    NAVER,
    KAKAO,
    LOCAL;

    @JsonCreator
    public static SocialType from(String value) {
        return SocialType.valueOf(value.toUpperCase());
    }
}
