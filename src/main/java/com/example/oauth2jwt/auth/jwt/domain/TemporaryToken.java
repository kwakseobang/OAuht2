package com.example.oauth2jwt.auth.jwt.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "temporary_token", timeToLive = 300L)  // ttl은 1L = 1초, expiredMs은 1000L = 1초
public class TemporaryToken {

    @Id
    String temporaryToken;

    String accessToken;

    @Builder
    public TemporaryToken(String temporaryToken, String accessToken) {
        this.temporaryToken = temporaryToken;
        this.accessToken = accessToken;
    }
}
