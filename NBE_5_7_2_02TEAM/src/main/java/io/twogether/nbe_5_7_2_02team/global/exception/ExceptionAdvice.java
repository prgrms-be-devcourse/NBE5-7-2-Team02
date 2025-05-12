package io.twogether.nbe_5_7_2_02team.global.exception;

import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    //    @ExceptionHandler(ErrorException.class)
    //    public Object handleException(
    //            ErrorException e,
    //            Model model,
    //            HandlerMethod handlerMethod) {
    //        boolean isApiRequest =
    //                AnnotatedElementUtils.hasAnnotation(handlerMethod.getMethod(),
    // ResponseBody.class);
    //        ErrorCode errorCode = e.getErrorCode();
    //
    //        log.error(errorCode.getMessage(), e);
    //
    //        if (isApiRequest) {
    //            HttpStatus httpStatus =
    //                    switch (errorCode.getErrorStatus()) {
    //                        case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
    //                        case NOT_FOUND -> HttpStatus.NOT_FOUND;
    //                        case CONFLICT -> HttpStatus.CONFLICT;
    //                        case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
    //                        case FORBIDDEN -> HttpStatus.FORBIDDEN;
    //                    };
    //
    //            return ResponseEntity.status(httpStatus)
    //                    .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage()));
    //        }
    //        else {
    //            model.addAttribute("message", errorCode.getMessage());
    //            model.addAttribute("url", e.getUrl());
    //            return "error/alert";
    //        }
    //    }

    @ExceptionHandler(ErrorException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleException(ErrorException e) {
        ErrorResponse error = new ErrorResponse("E001", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
