package com.example.oauth2jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import  com.example.oauth2jwt.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
