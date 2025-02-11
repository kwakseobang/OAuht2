package com.example.oauth2jwt.member.domain;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleType {
    MEMBER("ROLE_MEMBER", "일반 사용자 권한"),
    ADMIN("ROLE_ADMIN", "관리자 권한"),
    GUEST("ROLE_GUEST", "게스트 권한");

    private final String code;
    private final String displayName;

    public static RoleType of(String code) {
        return Arrays.stream(RoleType.values())
                .filter(r -> r.getCode().equals(code))
                .findAny()
                .orElse(GUEST);
    }
}
