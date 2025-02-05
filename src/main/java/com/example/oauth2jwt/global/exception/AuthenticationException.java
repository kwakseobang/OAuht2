package com.example.oauth2jwt.global.exception;

import com.example.oauth2jwt.global.error.ErrorCode;
import lombok.Getter;

@Getter
public class AuthenticationException extends CustomException {

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthenticationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
