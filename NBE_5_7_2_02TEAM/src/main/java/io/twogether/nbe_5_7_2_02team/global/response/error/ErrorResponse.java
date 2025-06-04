package io.twogether.nbe_5_7_2_02team.global.response.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;

@Getter
@JsonPropertyOrder({"code", "message", "errors"})
public class ErrorResponse<T> {
    private final String code;
    private final String message;

    // 추가적인 오류 세부 정보를 담기 위한 필드
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
