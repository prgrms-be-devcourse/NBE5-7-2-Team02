package io.twogether.nbe_5_7_2_02team.global.exception;

import static io.twogether.nbe_5_7_2_02team.global.exception.ErrorStatus.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 예시 ( 이해돕기용 )
    // ErrorStatus : 에러 상태 유형
    // code : 어떤 에러인지 쉽게 구별하는 용도
    NOT_READABLE_FILE(BAD_REQUEST, "MEMBER-001", "이미지 파일을 읽을 수 없습니다."),

    UNSUPPORTED_PROVIDER(BAD_REQUEST, "OAUTH-001", "지원하지 않는 OAuth2 제공자입니다.");

    private final ErrorStatus errorStatus;
    private final String code;
    private final String message;
}
