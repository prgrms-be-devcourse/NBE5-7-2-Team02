package io.twogether.nbe_5_7_2_02team.global.exception;

import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode;

import lombok.Getter;

@Getter
public class ErrorException extends RuntimeException {

    private final ErrorCode errorCode;

    public ErrorException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
