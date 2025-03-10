package com.example.oauth2jwt.global.exception;

import com.example.oauth2jwt.global.response.ResponseData;
import com.example.oauth2jwt.global.response.error.ErrorCode;
import java.nio.file.AccessDeniedException;
import javax.naming.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData<String>> handleException(Exception e) {
        return ResponseData.error(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseData<String>> handleUnauthorizedException(
        AuthenticationException e) {
        return ResponseData.error(ErrorCode.UNAUTHORIZED_ERROR, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseData<String>> handleForbiddenException(AccessDeniedException e) {
        return ResponseData.error(ErrorCode.FORBIDDEN_ERROR, e.getMessage());
    }

    // CustomException
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseData<String>> handleCustomException(CustomException ex) {
        return ResponseData.error(ex.getErrorCode(), ex.getMessage());
    }

}