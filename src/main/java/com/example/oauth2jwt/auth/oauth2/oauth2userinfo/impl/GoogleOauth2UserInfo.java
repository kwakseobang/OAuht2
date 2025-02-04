package com.example.oauth2jwt.auth.oauth2.oauth2userinfo.impl;

import com.example.oauth2jwt.auth.oauth2.oauth2userinfo.Oauth2UserInfo;
import java.util.Map;

public class GoogleOauth2UserInfo implements Oauth2UserInfo {
    private final Map<String, Object> attribute;
    // 구글의 경우에는, 네이버,카카오와는 달리 유저 정보가 감싸져 있지 않기 때문에,
    // 바로 get으로 유저 정보 Key를 사용해서 꺼내면 된다.

    public GoogleOauth2UserInfo(Map<String, Object> attribute) {

        this.attribute = attribute;
    }
    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attribute.get("sub").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }
}
/*
- 구글의 유저 정보 Response JSON 예시 -
{
   "sub": "식별값",
   "name": "name",
   "given_name": "given_name",
   "picture": "https//lh3.googleusercontent.com/~~",
   "email": "email",
   "email_verified": true,
   "locale": "ko"
}
 */