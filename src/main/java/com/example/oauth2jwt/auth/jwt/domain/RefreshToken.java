package com.example.oauth2jwt.auth.jwt.domain;

import com.example.oauth2jwt.member.domain.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class RefreshToken {

    @Id
    private Long memberId;

    @Column(name = "refreshtoken", nullable = false)
    private String refreshToken;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Builder(builderClassName = "SaveBuilder", builderMethodName = "SaveBuilder")
    public RefreshToken(
        String refreshToken,
        Long memberId,
        RoleType role
    ) {
        this.refreshToken = refreshToken;
        this.memberId = memberId;
        this.role = role;
    }

}
// PK -> memberId로 설정한 이유 - 아래 상황 방지

/*  1. 유효기간이 긴 Refresh Token이 탈취된 경우
    2. 탈취한 Refresh Token으로 정상 유저보다 먼저 Access Token을 재발급 받는 경우
    3. (토큰 탈취된 경우) 한 명의 사용자에 여러 refresh token 값이 저장되는 경우

    1) Refresh token rotation(RTR) 을 사용해 1번 문제를 해결할 수 있었다.
     RT가 탈취되더라도 AT를 재발급 받을 때마다 RT를 갱신해 기존 RT를 무효화 할 수 있었다.
     2) 3) 하지만 RTR 만으로는 2번 문제를 해결하지 못한다. 탈취범이 AT를 먼저 재발급 받아버리면, 오히려 정상 유저의 RT가 무효화 되는 꼴이 된다.
        정상 유저야 다시 로그인해서 refresh token을 발급받으면 되지만,
        해커는 기존에 탈취한 refresh token 을 지속적으로 재발급 받을 수 있는 가능성이 존재한다.
        또한 이렇게 되면 한 명의 사용자에 대해 여러개의 refresh token 이 생성되는 꼴이다.
*/