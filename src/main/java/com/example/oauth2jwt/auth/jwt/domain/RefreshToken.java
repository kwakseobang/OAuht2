package com.example.oauth2jwt.auth.jwt.domain;

import org.springframework.data.annotation.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refresh_token",timeToLive = 1209600L) // 2주
public class RefreshToken {

    @Id
    private Long memberId; // redis Key
    @Indexed
    private String refreshToken;
    @Indexed
    private String accessToken;


    @Builder(builderClassName = "SaveBuilder", builderMethodName = "SaveBuilder")
    public RefreshToken(String refreshToken,String accessToken, Long memberId) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.memberId = memberId;
    }


}
// "refresh_token"이라는 해시 이름(네임스페이스) 안에 여러 개의 memberId 값을 저장하게 됨.
/*
* Key: "refresh_token:{memberId}"
Value: { refreshToken: "value", accessToken: "value", ttl: "value" }
*
* */