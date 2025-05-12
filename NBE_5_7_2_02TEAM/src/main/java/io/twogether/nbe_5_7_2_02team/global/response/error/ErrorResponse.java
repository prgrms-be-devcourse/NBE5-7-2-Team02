package io.twogether.nbe_5_7_2_02team.global.response.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;

@Getter
@JsonPropertyOrder({"code", "message", "errors"})
public class ErrorResponse<T> {
    // 클라이언트에게 보내줄 에러응답을 정의한 클래스
    private final String code;
    private final String message;

    // 실제 발생한 오류들의 내용
    @JsonInclude(Include.NON_EMPTY)
    private final T errors;

    public ErrorResponse(String code, String message, T errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.errors = null;
    }
}
