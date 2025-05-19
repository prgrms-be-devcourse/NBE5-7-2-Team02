package io.twogether.nbe_5_7_2_02team.oauth.jwt;

import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode;
import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorResponse;
import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorStatus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        Object exceptionAttr = request.getAttribute("exception");

        // 예외 코드가 설정돼 있으면 해당 코드로 응답
        if (exceptionAttr instanceof ErrorCode errorCode) {
            response.setStatus(mapToHttpStatus(errorCode.getErrorStatus()));
            ErrorResponse<Void> errorResponse =
                    new ErrorResponse<>(errorCode.getCode(), errorCode.getMessage());
            response.getWriter().write(toJson(errorResponse));
        } else {
            // 기본 401 에러 처리
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ErrorResponse<Void> errorResponse = new ErrorResponse<>("AUTH-001", "인증이 필요합니다.");
            response.getWriter().write(toJson(errorResponse));
        }
    }

    private int mapToHttpStatus(ErrorStatus errorStatus) {
        return switch (errorStatus) {
            case BAD_REQUEST -> HttpServletResponse.SC_BAD_REQUEST;
            case UNAUTHORIZED -> HttpServletResponse.SC_UNAUTHORIZED;
            case FORBIDDEN -> HttpServletResponse.SC_FORBIDDEN;
            case NOT_FOUND -> HttpServletResponse.SC_NOT_FOUND;
            case CONFLICT -> HttpServletResponse.SC_CONFLICT;
        };
    }

    private String toJson(ErrorResponse<?> errorResponse) throws IOException {
        // ObjectMapper를 직접 써도 되고,
        return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(errorResponse);
    }
}
