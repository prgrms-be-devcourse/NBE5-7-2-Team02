package io.twogether.nbe_5_7_2_02team.global.exception;

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

    // ErrorException가 발생하면 해당 예외가 API 요청인지, 웹 요청인지 확인
    @ExceptionHandler(ErrorException.class)
    public Object handleException(
            ErrorException e,
            Model model,
            // 현재 실행 중인 컨트롤러 메서드 정보
            HandlerMethod handlerMethod) {
        // @ResponseBody 나 @RestController가 있으면 REST API 요청 아니면 웹요청
        boolean isApiRequest =
                AnnotatedElementUtils.hasAnnotation(handlerMethod.getMethod(), ResponseBody.class);
        ErrorCode errorCode = e.getErrorCode();

        // 에러 로깅
        log.error(errorCode.getMessage(), e);

        // API 요청인 경우 JSON 응답
        if (isApiRequest) {
            HttpStatus httpStatus =
                    switch (errorCode.getErrorStatus()) {
                        case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
                        case NOT_FOUND -> HttpStatus.NOT_FOUND;
                        case CONFLICT -> HttpStatus.CONFLICT;
                        case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
                        case FORBIDDEN -> HttpStatus.FORBIDDEN;
                    };

            return ResponseEntity.status(httpStatus)
                    .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage()));
        }
        // 웹 요청인 경우 React API(axios, fetch 사용 )
        else {
            model.addAttribute("message", errorCode.getMessage());
            model.addAttribute("url", e.getUrl());
            return "error/alert";
        }
    }
}
