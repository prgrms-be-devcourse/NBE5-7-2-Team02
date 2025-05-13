package io.twogether.nbe_5_7_2_02team.global.response.success;

import static io.twogether.nbe_5_7_2_02team.global.response.success.SuccessStatus.CREATED;
import static io.twogether.nbe_5_7_2_02team.global.response.success.SuccessStatus.OK;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    FOUND_MEMBER(OK, "MEMBER-200", "Found member"),
    CREATE_MEMBER(CREATED, "MEMBER-201", "Create member"),

    FOUND_FOLLOWER(OK, "FOLLOWER-200", "Found follower"),
    CREATE_FOLLOWER(CREATED, "FOLLOWER-201", "Create follower"),

    FOUND_CHATROOM(OK, "CHATROOM-200", "Found chatroom"),
    CREATE_CHATROOM(CREATED, "CHATROOM-201", "Create chatroom");

    private final SuccessStatus status;
    private final String code;
    private final String message;
}
