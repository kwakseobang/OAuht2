package com.example.oauth2jwt.auth.jwt.repository;

import com.example.oauth2jwt.auth.jwt.domain.RefreshToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
 // Redis에서는 CrudRepository를 사용한다.
//. @Id 또는 @Indexed 어노테이션을 적용한 프로퍼티들  CrudRepository가 제공하는 findBy~ 구문 사용 가능
public interface RefreshTokenRepository extends CrudRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByAccessToken(String accessToken);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}

/*
RefreshToken token = RefreshToken.SaveBuilder()
        .refreshToken("ref1234")
        .accessToken("acc5678")
        .memberId(1L)
        .ttl(3600L)
        .build();
refreshTokenRepository.save(token);
* */

/* 저장 구조
 Key: refresh_token:ref1234
Value: {
    "refreshToken": "ref1234",
    "accessToken": "acc5678",
    "memberId": 1,
    "ttl": 3600
}

* */