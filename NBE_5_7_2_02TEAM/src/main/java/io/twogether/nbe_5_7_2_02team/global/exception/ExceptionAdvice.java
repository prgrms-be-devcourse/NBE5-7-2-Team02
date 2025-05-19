package io.twogether.nbe_5_7_2_02team.global.exception;

import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode;
import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(ErrorException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse<?>> handleException(
            ErrorException e, HandlerMethod handlerMethod) {

        ErrorCode errorCode = e.getErrorCode();
        HttpStatus httpStatus =
                switch (errorCode.getErrorStatus()) {
                    case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
                    case NOT_FOUND -> HttpStatus.NOT_FOUND;
                    case CONFLICT -> HttpStatus.CONFLICT;
                    case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
                    case FORBIDDEN -> HttpStatus.FORBIDDEN;
                };

        log.error("[ExceptionAdvice] {}: {}", errorCode.getCode(), errorCode.getMessage(), e);

        return ResponseEntity.status(httpStatus)
                .body(new ErrorResponse<>(errorCode.getCode(), errorCode.getMessage()));
    }
}
