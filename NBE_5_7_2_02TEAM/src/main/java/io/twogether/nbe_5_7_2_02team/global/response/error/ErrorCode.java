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

    // OAUTH
    UNSUPPORTED_PROVIDER(BAD_REQUEST, "OAUTH-001", "지원하지 않는 OAuth2 제공자입니다."),
    OAUTH_TOKEN_ERROR(BAD_REQUEST, "OAUTH-002", "GitHub OAuth 인증 처리 중 오류가 발생했습니다."),
    OAUTH_USER_INFO_ERROR(BAD_REQUEST, "OAUTH-003", "GitHub 사용자 정보를 가져오는데 실패했습니다."),
    OAUTH_EMAIL_NOT_FOUND(BAD_REQUEST, "OAUTH-004", "GitHub 사용자 이메일 정보를 가져올 수 없습니다."),
    OAUTH_PRGRMS_ORG_REQUIRED(BAD_REQUEST, "OAUTH-005", "프로그래머스 교육 과정에 등록된 사용자만 가입할 수 있습니다."),

    // JWT
    INVALID_ACCESS_SIGNATURE(UNAUTHORIZED, "JWT-001", "잘못된 Access Token 서명입니다."),
    EXPIRED_ACCESS_TOKEN(UNAUTHORIZED, "JWT-002", "만료된 Access Token 토큰입니다."),
    UNSUPPORTED_ACCESS_TOKEN(UNAUTHORIZED, "JWT-003", "지원되지 않는 Access Token 토큰입니다."),
    INVALID_ACCESS_TOKEN(UNAUTHORIZED, "JWT-004", "유효하지 않은 Access Token 토큰입니다."),
    INVALID_REFRESH_TOKEN(UNAUTHORIZED, "JWT-005", "유효하지 않은 Refresh 토큰입니다."),
    INVALID_REFRESH_SIGNATURE(UNAUTHORIZED, "JWT-006", "잘못된 Refresh Token 서명입니다."),
    EXPIRED_REFRESH_TOKEN(UNAUTHORIZED, "JWT-007", "로그인이 만료되었습니다."),
    UNSUPPORTED_REFRESH_TOKEN(UNAUTHORIZED, "JWT-008", "지원되지 않는 Refresh Token 토큰입니다."),

    // IMAGE
    IMAGE_UPLOAD_LIMIT_EXCEEDED(BAD_REQUEST, "IMAGE-001", "이미지는 최대 10장까지만 업로드할 수 있습니다."),
    IMAGE_UPLOAD_FAILED(BAD_REQUEST, "IMAGE-002", "이미지 저장 중 오류가 발생했습니다."),
    IMAGE_DELETE_FAILED(BAD_REQUEST, "IMAGE-003", "기존 이미지 삭제 중 오류가 발생했습니다."),

    // POST
    NOT_FOUND_POST(NOT_FOUND, "POST-001", "해당 게시글이 존재하지 않습니다."),
    UNAUTHORIZED_POST_ACCESS(UNAUTHORIZED, "POST-002", "게시글에 대한 권한이 없습니다."),
    LIKE_ALREADY_EXIST(BAD_REQUEST, "POST-003", "좋아요가 이미 존재합니다."),
    NOT_FOUND_LIKE(NOT_FOUND, "POST-004", "해당 좋아요가 존재하지 않습니다."),
    RECRUITMENT_NOT_AVAILABLE(BAD_REQUEST, "POST-005", "모집 중인 게시글에만 지원 가능합니다."),
    NOT_FOUND_RECRUITMENT_FIELD(NOT_FOUND, "POST-006", "해당 모집 분야가 존재하지 않습니다."),
    RECRUITMENT_CLOSED(BAD_REQUEST, "POST-007", "해당 모집 분야는 마감되었습니다."),
    ALREADY_APPLIED(BAD_REQUEST, "POST-008", "이미 지원한 분야입니다."),
    CANNOT_APPLY_TO_OWN_POST(BAD_REQUEST, "POST-009", "본인 글에는 지원할 수 없습니다."),

    // CHAT MESSAGE
    CHAT_MESSAGE_CONTENT_BLANK(BAD_REQUEST, "CHAT-00", "메세지 내용이 비어있습니다."),
    CHAT_MESSAGE_NOT_FOUND(NOT_FOUND, "CHAT-00", "메세지를 찾을 수 없습니다."),

    // CHAT MEMBER
    CHAT_MEMBER_ALREADY_EXISTS(BAD_REQUEST, "CHAT-MEMBER-001", "이미 채팅방에 참여중입니다."),
    CHAT_MEMBER_NOT_LOGIN(BAD_REQUEST, "CHAT-MEMBER-002", "로그인 후 참여 가능합니다."),
    CHAT_ROOM_EMPTY(NOT_FOUND, "CHAT-MEMBER-003", "채팅방에 참여 중인 인원이 없습니다.."),
    CHAT_MEMBER_NOT_ENTER(NOT_FOUND, "CHAT-MEMBER-004", "채팅방에 참여 중이 아닙니다."),
    CHAT_MEMBER_UNDEFINED_STATUS(BAD_REQUEST, "CHAT-MEMBER-005", "잘못된 상태 값 입니다."),

    // CHATROOM
    CHAT_ROOM_LIST_EMPTY(NOT_FOUND, "CHATROOM-001", "목록에 채팅창이 존재하지 않습니다."),
    CHAT_ROOM_ALREADY_EXISTS(BAD_REQUEST, "CHATROOM-002", "채팅방이 이미 존재합니다."),
    CHAT_ROOM_NOT_FOUND(NOT_FOUND, "CHATROOM-003", "채팅방을 찾을 수 없습니다.");

    private final ErrorStatus errorStatus;
    private final String code;
    private final String message;
}
