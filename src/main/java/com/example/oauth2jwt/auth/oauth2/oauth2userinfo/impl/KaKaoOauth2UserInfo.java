package com.example.oauth2jwt.auth.oauth2.oauth2userinfo.impl;

import com.example.oauth2jwt.auth.oauth2.oauth2userinfo.Oauth2UserInfo;
import java.util.Map;

// kakao 항목 에러 name
public class KaKaoOauth2UserInfo implements Oauth2UserInfo {
    private final Map<String, Object> attribute;

    private final String email;

    // / attributes 맵의 kakao_account 키의 값에 실제 attributes 맵이 할당되어 있음
    // 카카오의 경우에는, 유저 정보가 'kakao_account.profile'으로 2번 감싸져있는 구조이다.
    public KaKaoOauth2UserInfo(Map<String, Object> attribute) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
        this.attribute = kakaoProfile;
        this.email = kakaoAccount.get("email").toString();
    }
    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return attribute.get("nickname").toString();
    }
}

/*
- 카카오의 유저 정보 Response JSON 예시 -
{
    "id":123456789,
    "connected_at": "2022-04-11T01:45:28Z",
    "kakao_account": {
        // 프로필 또는 닉네임 동의 항목 필요
        "profile_nickname_needs_agreement": false,
        // 프로필 또는 프로필 사진 동의 항목 필요
        "profile_image_needs_agreement	": false,
        "profile": {
            // 프로필 또는 닉네임 동의 항목 필요
            "nickname": "홍길동",
            // 프로필 또는 프로필 사진 동의 항목 필요
            "thumbnail_image_url": "http://yyy.kakao.com/.../img_110x110.jpg",
            "profile_image_url": "http://yyy.kakao.com/dn/.../img_640x640.jpg",
            "is_default_image":false
        },
        // 이름 동의 항목 필요
        "name_needs_agreement":false,
        "name":"홍길동",
        // 카카오계정(이메일) 동의 항목 필요
        "email_needs_agreement":false,
        "is_email_valid": true,
        "is_email_verified": true,
        "email": "sample@sample.com",
        // 연령대 동의 항목 필요
        "age_range_needs_agreement":false,
        "age_range":"20~29",
        // 출생 연도 동의 항목 필요
        "birthyear_needs_agreement": false,
        "birthyear": "2002",
        // 생일 동의 항목 필요
        "birthday_needs_agreement":false,
        "birthday":"1130",
        "birthday_type":"SOLAR",
        // 성별 동의 항목 필요
        "gender_needs_agreement":false,
        "gender":"female",
        // 카카오계정(전화번호) 동의 항목 필요
        "phone_number_needs_agreement": false,
        "phone_number": "+82 010-1234-5678",
        // CI(연계정보) 동의 항목 필요
        "ci_needs_agreement": false,
        "ci": "${CI}",
        "ci_authenticated_at": "2019-03-11T11:25:22Z",
    },
    "properties":{
        "${CUSTOM_PROPERTY_KEY}": "${CUSTOM_PROPERTY_VALUE}",
        ...
    }
}
 */