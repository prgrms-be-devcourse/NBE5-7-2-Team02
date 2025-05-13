package io.twogether.nbe_5_7_2_02team.global.response.error;

import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorStatus.*;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // MEMBER
    NOT_FOUND_MEMBER(NOT_FOUND, "MEMBER-001", "존재하지 않는 사용자 입니다."),

    // FOLLOW
    NOT_FOUND_FOLLOWER(NOT_FOUND, "FOLLOW-001", "팔로우 요청자 정보가 없습니다."),
    NOT_FOUND_FOLLOWING(NOT_FOUND, "FOLLOW-002", "팔로잉 대상 정보가 없습니다."),
    NOT_YOURSELF_FOLLOW(BAD_REQUEST, "FOLLOW-003", "자기 자신은 팔로우할 수 없습니다."),
    NOT_DUPLICATION_FOLLOW(BAD_REQUEST, "FOLLOW-004", "이미 팔로우한 사용자 입니다."),

    // IMAGE
    IMAGE_UPLOAD_LIMIT_EXCEEDED(BAD_REQUEST, "IMAGE-001", "이미지는 최대 10장까지만 업로드할 수 있습니다."),
    IMAGE_UPLOAD_FAILED(BAD_REQUEST, "IMAGE-002", "이미지 저장 중 오류가 발생했습니다."),
  
    // CHATROOM
    CHAT_ROOM_LIST_EMPTY(NOT_FOUND, "CHATROOM-001", "목록에 채팅창이 존재하지 않습니다."),
    POST_NOT_FOUND(NOT_FOUND, "CHATROOM-002", "게시글을 찾을 수 없습니다."),
    CHAT_ROOM_ALREADY_EXISTS(BAD_REQUEST, "CHATROOM-003", "채팅방이 이미 존재합니다."),
    CHATROOM_NOT_FOUND(NOT_FOUND, "CHATROOM-004", "채팅방을 찾을 수 없습니다.");

    private final ErrorStatus errorStatus;
    private final String code;
    private final String message;
}
