package com.example.oauth2jwt.global.response.responseItem;

import com.example.oauth2jwt.global.response.responseItem.StatusCode;
import com.example.oauth2jwt.global.response.responseItem.SuccessMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode {

    // Member 관련 성공 응답
    CREATED_MEMBER(StatusCode.CREATED, SuccessMessage.CREATED_MEMBER),
    READ_MEMBER(StatusCode.OK, SuccessMessage.READ_MEMBER),
    UPDATE_MEMBER(StatusCode.NO_CONTENT, SuccessMessage.UPDATE_MEMBER),
    DELETE_MEMBER(StatusCode.NO_CONTENT, SuccessMessage.DELETE_MEMBER),

    // Token 성공 응답
    REISSUE_SUCCESS(StatusCode.OK, SuccessMessage.REISSUE_SUCCESS),
    // 토큰 유효 응답
    TOKEN_IS_VALID(StatusCode.OK, SuccessMessage.TOKEN_IS_VALID),

    // 기타 성공 응답
    READ_IS_LOGIN(StatusCode.OK, SuccessMessage.READ_IS_LOGIN),
    LOGIN_SUCCESS(StatusCode.OK, SuccessMessage.LOGIN_SUCCESS),
    USERNAME_SUCCESS(StatusCode.OK, SuccessMessage.USERNAME_SUCCESS),
    NICKNAME_SUCCESS(StatusCode.OK, SuccessMessage.NICKNAME_SUCCESS),
    LOGOUT_SUCCESS(StatusCode.OK, SuccessMessage.LOGOUT_SUCCESS),
    UPDATE_PASSWORD(StatusCode.NO_CONTENT, SuccessMessage.UPDATE_PASSWORD),

    ;


    private  int httpStatus;
    private String message;

}

