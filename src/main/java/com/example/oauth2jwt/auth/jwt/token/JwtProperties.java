package com.example.oauth2jwt.auth.jwt.token;

import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtProperties {
    // @RequiredArgsConstructor 와 @Value는 같이 사용 못한다.  컴파일 타임에 Lombok과 충돌이 생길 수 있다.
    @Value("${spring.jwt.secretKey}")
    private String secret;
    @Value("${spring.jwt.access.expiration}")
    Long accessTokenExpireTime;
    @Value("${spring.jwt.refresh.expiration}")
    Long refreshTokenExpireTime;
    public SecretKey getSecretKey() {
        return new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    public Long getAccessTokenExpireTime() {
        return  accessTokenExpireTime;
    }

    public Long getRefreshTokenExpireTime() {
        return refreshTokenExpireTime;
    }
}
