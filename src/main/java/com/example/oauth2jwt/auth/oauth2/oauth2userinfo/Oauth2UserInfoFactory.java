package com.example.oauth2jwt.auth.oauth2.oauth2userinfo;

import com.example.oauth2jwt.auth.oauth2.domain.SocialType;
import com.example.oauth2jwt.auth.oauth2.oauth2userinfo.impl.GoogleOauth2UserInfo;
import com.example.oauth2jwt.auth.oauth2.oauth2userinfo.impl.KaKaoOauth2UserInfo;
import com.example.oauth2jwt.auth.oauth2.oauth2userinfo.impl.NaverOauth2UserInfo;
import java.util.Map;

public class Oauth2UserInfoFactory {
    public static Oauth2UserInfo getOAuth2UserInfo(
            SocialType socialType,
            Map<String, Object> attributes
    ) {
        switch (socialType) {
            case GOOGLE: return new GoogleOauth2UserInfo(attributes);
            case NAVER: return new NaverOauth2UserInfo(attributes);
            case KAKAO: return new KaKaoOauth2UserInfo(attributes);
            default: throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}