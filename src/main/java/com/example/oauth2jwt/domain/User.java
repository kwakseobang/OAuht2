package com.example.oauth2jwt.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String name;

    private String email;

    private String role;


    @Builder
    public User(
            String username,
            String name,
            String email,
            String role
    ){
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public void updateEmail(String email) {
        this.email = email;
    }
    public void updateName(String name) {
        this.name = name;
    }
}
