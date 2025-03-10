package com.example.oauth2jwt.global.response.error;

import com.example.oauth2jwt.global.response.StatusCode;
import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode implements StatusCode {

    // Member 관련 실패 응답
    NOT_FOUND_MEMBER(
        HttpStatus.NOT_FOUND.value(),
        ErrorMessage.NOT_FOUND_MEMBER.getMessage()
    ),
    BAD_REQUEST_MEMBER(
        HttpStatus.BAD_REQUEST.value(),
        ErrorMessage.BAD_REQUEST_MEMBER.getMessage()
    ),
    BAD_REQUEST_PASSWORD(
        HttpStatus.BAD_REQUEST.value(),
        ErrorMessage.BAD_REQUEST_PASSWORD.getMessage()
    ),
    DUPLICATE_USERNAME(
        HttpStatus.BAD_REQUEST.value(),
        ErrorMessage.DUPLICATE_USERNAME.getMessage()
    ),
    DUPLICATE_NICKNAME(
        HttpStatus.BAD_REQUEST.value(),
        ErrorMessage.DUPLICATE_NICKNAME.getMessage()
    ),
    // Token 실패 응답
    TOKEN_EXPIRED(
        HttpStatus.UNAUTHORIZED.value(),
        ErrorMessage.TOKEN_EXPIRED.getMessage()
    ),
    TOKEN_ERROR(
        HttpStatus.UNAUTHORIZED.value(),
        ErrorMessage.TOKEN_ERROR.getMessage()
    ),
    BAD_REQUEST_TOKEN(
        HttpStatus.BAD_REQUEST.value(),
        ErrorMessage.BAD_REQUEST_TOKEN.getMessage()
    ),
    TOKEN_IS_BLACKLIST(
        HttpStatus.UNAUTHORIZED.value(),
        ErrorMessage.TOKEN_IS_BLACKLIST.getMessage()
    ),
    TOKEN_HASH_NOT_SUPPORTED(
        HttpStatus.UNAUTHORIZED.value(),
        ErrorMessage.TOKEN_HASH_NOT_SUPPORTED.getMessage()
    ),
    WRONG_AUTH_HEADER(
        HttpStatus.UNAUTHORIZED.value(),
        ErrorMessage.WRONG_AUTH_HEADER.getMessage()
    ),
    TOKEN_VALIDATION_TRY_FAILED(
        HttpStatus.UNAUTHORIZED.value(),
        ErrorMessage.TOKEN_VALIDATION_TRY_FAILED.getMessage()
    ),
    // 기타 실패 응답
    PREVENT_GET_ERROR(
        HttpStatus.NO_CONTENT.value(),
        ErrorMessage.PREVENT_GET_ERROR.getMessage()
    ),
    INTERNAL_SERVER_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        ErrorMessage.INTERNAL_SERVER_ERROR.getMessage()
    ),
    UNAUTHORIZED_ERROR(
        HttpStatus.UNAUTHORIZED.value(),
        ErrorMessage.UNAUTHORIZED.getMessage()
    ),
    FORBIDDEN_ERROR(
        HttpStatus.FORBIDDEN.value(),
        ErrorMessage.FORBIDDEN.getMessage()
    ),
    ;

    private final int httpStatus;
    private final String message;

}