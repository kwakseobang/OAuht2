package com.example.oauth2jwt.global.response.success;

import com.example.oauth2jwt.global.response.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum SuccessCode implements StatusCode {

    // Member 관련 성공 응답
    CREATED_MEMBER(
        HttpStatus.CREATED.value(),
        SuccessMessage.CREATED_MEMBER.getMessage()
    ),
    READ_MEMBER(
        HttpStatus.OK.value(),
        SuccessMessage.READ_MEMBER.getMessage()
    ),
    UPDATE_MEMBER(
        HttpStatus.NO_CONTENT.value(),
        SuccessMessage.UPDATE_MEMBER.getMessage()
    ),
    DELETE_MEMBER(
        HttpStatus.NO_CONTENT.value(),
        SuccessMessage.DELETE_MEMBER.getMessage()
    ),

    // Token 성공 응답
    REISSUE_SUCCESS(
        HttpStatus.OK.value(),
        SuccessMessage.REISSUE_SUCCESS.getMessage()
    ),
    ACCESS_TOKEN_SUCCESS(
        HttpStatus.OK.value(),
        SuccessMessage.ACCESS_TOKEN_SUCCESS.getMessage()
    ),
    // 토큰 유효 응답
    TOKEN_IS_VALID(
        HttpStatus.OK.value(),
        SuccessMessage.TOKEN_IS_VALID.getMessage()
    ),
    // 기타 성공 응답
    READ_IS_LOGIN(
        HttpStatus.OK.value(),
        SuccessMessage.READ_IS_LOGIN.getMessage()
    ),
    LOGIN_SUCCESS(
        HttpStatus.OK.value(),
        SuccessMessage.LOGIN_SUCCESS.getMessage()
    ),
    USERNAME_SUCCESS(
        HttpStatus.OK.value(),
        SuccessMessage.USERNAME_SUCCESS.getMessage()
    ),
    NICKNAME_SUCCESS(
        HttpStatus.OK.value(),
        SuccessMessage.NICKNAME_SUCCESS.getMessage()
    ),
    LOGOUT_SUCCESS(
        HttpStatus.OK.value(),
        SuccessMessage.LOGOUT_SUCCESS.getMessage()
    ),
    UPDATE_PASSWORD(
        HttpStatus.NO_CONTENT.value(),
        SuccessMessage.UPDATE_PASSWORD.getMessage()
    ),
    ;

    private final int httpStatus;
    private final String message;

}