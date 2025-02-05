package com.example.oauth2jwt.global.error;

import com.example.oauth2jwt.global.response.responseItem.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // Member 관련 실패 응답
    NOT_FOUND_MEMBER(StatusCode.NOT_FOUND, ErrorMessage.NOT_FOUND_USER),
    BAD_REQUEST_MEMBER(StatusCode.BAD_REQUEST, ErrorMessage.BAD_REQUEST_USER),
    BAD_REQUEST_PASSWORD(StatusCode.BAD_REQUEST, ErrorMessage.BAD_REQUEST_PASSWORD),
    DUPLICATE_USERNAME(StatusCode.BAD_REQUEST, ErrorMessage.DUPLICATE_USERNAME),
    DUPLICATE_NICKNAME(StatusCode.BAD_REQUEST, ErrorMessage.DUPLICATE_NICKNAME),


    // Token 실패 응답
    TOKEN_EXPIRED(StatusCode.TOKEN_EXPIRED, ErrorMessage.TOKEN_EXPIRED),
    TOKEN_ERROR(StatusCode.TOKEN_ERROR, ErrorMessage.TOKEN_ERROR),
    BAD_REQUEST_TOKEN(StatusCode.BAD_REQUEST, ErrorMessage.BAD_REQUEST_TOKEN),
    TOKEN_IS_BLACKLIST(StatusCode.TOKEN_IS_BLACKLIST,ErrorMessage.TOKEN_IS_BLACKLIST),
    TOKEN_HASH_NOT_SUPPORTED(StatusCode.TOKEN_HASH_NOT_SUPPORTED,ErrorMessage.TOKEN_HASH_NOT_SUPPORTED),
    WRONG_AUTH_HEADER(StatusCode.NO_AUTH_HEADER,ErrorMessage.WRONG_AUTH_HEADER),
    TOKEN_VALIDATION_TRY_FAILED(StatusCode.TOKEN_VALIDATION_TRY_FAILED,ErrorMessage.TOKEN_VALIDATION_TRY_FAILED),

    // 기타 실패 응답
    PREVENT_GET_ERROR(StatusCode.NO_CONTENT, ErrorMessage.PREVENT_GET_ERROR),
    INTERNAL_SERVER_ERROR(StatusCode.INTERNAL_SERVER_ERROR, ErrorMessage.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED_ERROR(StatusCode.UNAUTHORIZED, ErrorMessage.UNAUTHORIZED),
    FORBIDDEN_ERROR(StatusCode.FORBIDDEN, ErrorMessage.FORBIDDEN),


    ;


    private  int httpStatus;
    private String message;

}
