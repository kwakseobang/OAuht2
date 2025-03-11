package com.example.oauth2jwt.member.exception;

import com.example.oauth2jwt.global.exception.CustomException;
import com.example.oauth2jwt.global.response.error.ErrorCode;
import lombok.Getter;

@Getter
public class MemberException extends CustomException {

    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MemberException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}