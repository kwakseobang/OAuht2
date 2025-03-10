package com.example.oauth2jwt.global.response.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorMessage {

    // < User -> Error >
    NOT_FOUND_MEMBER("ERROR - 회원을 찾을 수 없습니다."),
    BAD_REQUEST_MEMBER("ERROR - 잘못된 회원 요청 에러"),
    BAD_REQUEST_PASSWORD("ERROR - 잘못된 비밀번호 요청 에러"),
    DUPLICATE_USERNAME("ERROR - 회원가입 ID 중복 에러"),
    DUPLICATE_NICKNAME("ERROR - 회원가입 닉네임 중복"),
    UNAUTHORIZED("ERROR - Unauthorized 에러"), // 인증되지 않은 사용자
    FORBIDDEN("ERROR - Forbidden 에러"), // 권한 없을 때
    PREVENT_GET_ERROR("Status 204 - 리소스 및 리다이렉트 GET 호출 에러"),
    // < Token >
    TOKEN_EXPIRED("ERROR - JWT 토큰 만료 에러"),
    TOKEN_ERROR("ERROR - 잘못된 JWT 토큰 에러"),
    BAD_REQUEST_TOKEN("ERROR - 잘못된 토큰 요청 에러"),
    TOKEN_IS_BLACKLIST("ERROR - 폐기된 토큰"),
    TOKEN_HASH_NOT_SUPPORTED("ERROR - 지원하지 않는 형식의 토큰"),
    WRONG_AUTH_HEADER("ERROR - [Bearer ]로 시작하는 토큰이 없습니다."),
    TOKEN_VALIDATION_TRY_FAILED("ERROR - 토큰 인증 실패"),
    // < Etc >
    INTERNAL_SERVER_ERROR("ERROR - 서버 내부 에러"),
    ;

    private final String message;

}