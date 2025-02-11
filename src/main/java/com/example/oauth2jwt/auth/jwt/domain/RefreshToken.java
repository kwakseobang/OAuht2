package com.example.oauth2jwt.auth.jwt.domain;

import jakarta.persistence.Entity;
import org.springframework.data.annotation.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refresh_token")
public class RefreshToken {

    @Id
    private Long memberId; // redis Key
    @Indexed
    private String refreshToken;
    @Indexed
    private String accessToken;

    @TimeToLive
    private Long ttl;

    @Builder(builderClassName = "SaveBuilder", builderMethodName = "SaveBuilder")
    public RefreshToken(String refreshToken,String accessToken, Long memberId, Long ttl) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.memberId = memberId;
        this.ttl = ttl;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
// "refresh_token"이라는 해시 이름(네임스페이스) 안에 여러 개의 memberId 값을 저장하게 됨.
/*
* Key: "refresh_token:{memberId}"
Value: { refreshToken: "value", accessToken: "value", ttl: "value" }
*
* */