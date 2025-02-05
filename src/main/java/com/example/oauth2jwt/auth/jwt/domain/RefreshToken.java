package com.example.oauth2jwt.auth.jwt.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class RefreshToken {

    @Id
    private String token;

    private Long memberId;


    @Builder(builderClassName = "RefreshTokenSaveBuilder", builderMethodName = "RefreshTokenSaveBuilder")
    public RefreshToken(String token,Long memberId) {
        this.token = token;
        this.memberId = memberId;
    }
}

