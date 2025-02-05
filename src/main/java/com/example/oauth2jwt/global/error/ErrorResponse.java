package com.example.oauth2jwt.global.error;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@ToString
public class ErrorResponse<T> {
    private final int status;
    private final String message;
    private final ErrorCode code;
    private T data;
    // response data가 없을때
    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getHttpStatus())
                        .message(errorCode.getMessage())
                        .code(errorCode)
                        .build()
                );
    }

    // response data가 있을때
    public static <T> ResponseEntity<ErrorResponse<T>> toResponseEntity(
            ErrorCode errorCode, T data) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.<T>builder()
                        .status(errorCode.getHttpStatus())
                        .message(errorCode.getMessage())
                        .code(errorCode)
                        .data(data)
                        .build()
                );
    }
}
