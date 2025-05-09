package io.twogether.nbe_5_7_2_02team.global.exception;

import static io.twogether.nbe_5_7_2_02team.global.exception.ErrorStatus.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    NOT_FOUND_MEMBER(NOT_FOUND, "MEMBER-001", "존재하지 않는 사용자 입니다."),

    NOT_FOUND_FOLLOWER(NOT_FOUND, "FOLLOW-001", "팔로우 요청자 정보가 없습니다."),
    NOT_FOUND_FOLLOWING(NOT_FOUND, "FOLLOW-002", "팔로잉 대상 정보가 없습니다."),
    NOT_YOURSELF_FOLLOW(BAD_REQUEST, "FOLLOW-003", "자기 자신은 팔로우할 수 없습니다."),
    NOT_DUPLICATION_FOLLOW(BAD_REQUEST, "FOLLOW-004", "이미 팔로우한 사용자 입니다.");

    private final ErrorStatus errorStatus;
    private final String code;
    private final String message;
}
