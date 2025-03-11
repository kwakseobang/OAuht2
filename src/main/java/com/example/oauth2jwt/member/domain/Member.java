package com.example.oauth2jwt.member.domain;


import com.example.oauth2jwt.global.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "member")
@Getter
@NoArgsConstructor
@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType role;

    // 빌더 패턴에서 값이 들어오지 않을 경우 기본 타입은 0,false 같은 타입 저장, 참조 타입은 null
    @Builder(builderClassName = "SaveBuilder", builderMethodName = "SaveBuilder")
    public Member(
        Long id, String username,
        String password,
        String nickname,
        RoleType role
        ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

}