package com.example.oauth2jwt.member.domain;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleType {

    MEMBER("ROLE_MEMBER"),
    ADMIN("ROLE_ADMIN"),
    GUEST("ROLE_GUEST"),
    ;

    private final String value;

    public static RoleType of(String code) {
        return Arrays.stream(RoleType.values())
            .filter(r -> r.getValue().equals(code))
            .findAny()
            .orElse(GUEST);
    }

}