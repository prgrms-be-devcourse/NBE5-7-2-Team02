package io.twogether.nbe_5_7_2_02team.global.exception;

import lombok.Getter;

@Getter
public class ErrorException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String url;

    public ErrorException(ErrorCode errorCode, String url) {
        this.errorCode = errorCode;
        this.url = url;
    }

}
