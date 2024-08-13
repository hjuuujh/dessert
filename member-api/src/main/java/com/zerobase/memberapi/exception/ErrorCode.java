package com.zerobase.memberapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),

    // 회원가입
    ALREADY_REGISTERED_USER(HttpStatus.BAD_REQUEST, "이미 가입된 이메일입니다.");


    private final HttpStatus httpStatus;
    private final String description;
}