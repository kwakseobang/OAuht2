package com.example.oauth2jwt.auth.jwt.repository;

import com.example.oauth2jwt.auth.jwt.domain.RefreshToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

}