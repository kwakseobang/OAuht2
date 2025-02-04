package com.example.oauth2jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing //  Auditing 기능을 활성화
public class OAuth2JwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(OAuth2JwtApplication.class, args);
    }

}
