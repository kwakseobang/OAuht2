package com.example.oauth2jwt.auth.jwt.repository;

import com.example.oauth2jwt.auth.jwt.domain.TemporaryToken;
import org.springframework.data.repository.CrudRepository;

public interface TemporaryTokenRepository extends CrudRepository<TemporaryToken,String>{
}
