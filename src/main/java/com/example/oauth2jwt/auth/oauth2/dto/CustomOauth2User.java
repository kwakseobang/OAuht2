package com.example.oauth2jwt.auth.oauth2.dto;

import com.example.oauth2jwt.member.domain.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

// OAuth2 로그인 성공 시 사용자 정보를 저장하는 역할을 한다.
// OAuth2에서 제공하는 기본 정보만 사용할거면 DefaultOAuth2User를 사용해도 된다. 그 외 추가할 정보가 있으면 CustomOauth2User를 만든다.
// 해당 클래스에 유저 정보를 담아서 SecurityContext에 저장한다.
public class CustomOauth2User implements OAuth2User {

    private final Member member;
    private final Map<String, Object> attributes;


    public CustomOauth2User(Member member, Map<String,Object> attributes) {
        this.member = member;
        this.attributes =attributes;
    }
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return member.getRoleType().getCode();
            }
        });

        return collection;
    }

    @Override
    public String getName() {
        return member.getNickname();
    }
    public String getUsername() {
        return member.getUsername();
    }

    public Long getId() {
        return member.getId();
    }
}
