package io.twogether.nbe_5_7_2_02team.global.exception;

import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode;
import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    // 나중에 화면이 구성이 된다면 html(view) 예외처리와 rest Api 예외에 따라서 처리하는 코드
//        @ExceptionHandler(ErrorException.class)
//        public Object handleException(
//                ErrorException e,
//                Model model,
//                HandlerMethod handlerMethod) {
//            boolean isApiRequest =
//                    AnnotatedElementUtils.hasAnnotation(handlerMethod.getMethod(),
//     ResponseBody.class);
//            ErrorCode errorCode = e.getErrorCode();
//
//            log.error(errorCode.getMessage(), e);
//
//            if (isApiRequest) {
//                HttpStatus httpStatus =
//                        switch (errorCode.getErrorStatus()) {
//                            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
//                            case NOT_FOUND -> HttpStatus.NOT_FOUND;
//                            case CONFLICT -> HttpStatus.CONFLICT;
//                            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
//                            case FORBIDDEN -> HttpStatus.FORBIDDEN;
//                        };
//
//                return ResponseEntity.status(httpStatus)
//                        .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage()));
//            }
//            else {
//                model.addAttribute("message", errorCode.getMessage());
//                model.addAttribute("url", e.getUrl());
//                return "error/alert";
//            }
//        }

    // 현재 view쪽이 구현되지 않아 임시로 사용하는 postman,local테스트 예외코드
    @ExceptionHandler(ErrorException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleException(ErrorException e) {
        ErrorCode errorCode = e.getErrorCode();

        log.error("[ExceptionAdvice] Error handled: {}",errorCode.getMessage(), e);

        HttpStatus httpStatus = switch (errorCode.getErrorStatus()) {
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
        };

        return ResponseEntity.status(httpStatus)
            .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage()));
    }
}
