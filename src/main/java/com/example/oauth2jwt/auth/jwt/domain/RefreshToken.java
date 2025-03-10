package com.example.oauth2jwt.auth.jwt.domain;

import jakarta.persistence.Column;
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
    private String refreshToken;

    @Column(name = "member_id", nullable = false)
    private Long memberId;


    @Builder(builderClassName = "SaveBuilder", builderMethodName = "SaveBuilder")
    public RefreshToken(String refreshToken, Long memberId) {
        this.refreshToken = refreshToken;
        this.memberId = memberId;
    }

}