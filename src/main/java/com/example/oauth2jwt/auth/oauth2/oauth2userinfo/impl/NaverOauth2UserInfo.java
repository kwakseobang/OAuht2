package com.example.oauth2jwt.auth.oauth2.oauth2userinfo.impl;

import com.example.oauth2jwt.auth.oauth2.oauth2userinfo.Oauth2UserInfo;
import java.util.Map;

public class NaverOauth2UserInfo implements Oauth2UserInfo {
    private final Map<String, Object> attribute;

    // naver는 유저 정보가 response로 한번 더 감싸져있음.
    public NaverOauth2UserInfo(Map<String, Object> attribute) {

        this.attribute = (Map<String, Object>) attribute.get("response");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
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
- 네이버의 유저 정보 Response JSON 예시 -
{
  "resultcode": "00",
  "message": "success",
  "response": {
    "email": "openapi@naver.com",
    "nickname": "OpenAPI",
    "profile_image": "https://ssl.pstatic.net/static/pwe/address/nodata_33x33.gif",
    "age": "40-49",
    "gender": "F",
    "id": "32742776",
    "name": "오픈 API",
    "birthday": "10-01"
  }
}
 */