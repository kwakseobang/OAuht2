package com.example.oauth2jwt.auth.jwt.repository;

import com.example.oauth2jwt.auth.jwt.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken,String> {

}
