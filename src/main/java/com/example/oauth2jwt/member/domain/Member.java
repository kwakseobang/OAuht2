package com.example.oauth2jwt.member.domain;


import com.example.oauth2jwt.global.domain.BaseEntity;
import com.example.oauth2jwt.auth.oauth2.domain.SocialType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username",nullable = false,length = 50)
    private String username;

    @Column(name = "password",nullable = true)
    private String password;

    @Column(name = "nickname",nullable = false)
    private String nickname;

    @Column(name = "role_type",nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Column(name = "profile_img_url",nullable = true)
    private String profileImgUrl;
    @Column(name = "social_login_id",nullable = true)
    private String socialLoginId;
    @Column(name = "social_type",nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialType socialType; // 일반 로그인일 경우 Local


    @Builder
    public Member(
            String username,
            String password,
            String nickname,
            RoleType roleType,
            String profileImgUrl,
            String socialLoginId,
            SocialType socialType
    ) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.roleType = roleType;
        this.profileImgUrl = profileImgUrl;
        this.socialLoginId = socialLoginId;
        this.socialType = socialType;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
