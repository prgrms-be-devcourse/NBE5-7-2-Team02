package io.twogether.nbe_5_7_2_02team.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"code", "messeage", "errors"})
public class ErrorResponse<T> {
    // 클라이언트에게 보내줄 에러응답을 정의한 클래스
    private final String code;
    private final String messeage;

    // 실제 발생한 오류들의 내용
    @JsonInclude(Include.NON_EMPTY)
    private final T errors;

    public ErrorResponse(String code, String messeage, T errors) {
        this.code = code;
        this.messeage = messeage;
        this.errors = errors;
    }

    public ErrorResponse(String code, String messeage) {
        this.code = code;
        this.messeage = messeage;
        this.errors = null;
    }

}
