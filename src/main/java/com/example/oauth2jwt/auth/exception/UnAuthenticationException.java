package com.example.oauth2jwt.auth.exception;

import com.example.oauth2jwt.global.exception.CustomException;
import com.example.oauth2jwt.global.response.error.ErrorCode;
import lombok.Getter;

@Getter
public class UnAuthenticationException extends CustomException {

    public UnAuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnAuthenticationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}