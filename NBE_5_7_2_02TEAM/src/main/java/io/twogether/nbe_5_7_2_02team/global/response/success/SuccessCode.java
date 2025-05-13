package io.twogether.nbe_5_7_2_02team.global.response.success;

import static io.twogether.nbe_5_7_2_02team.global.response.success.SuccessStatus.CREATED;
import static io.twogether.nbe_5_7_2_02team.global.response.success.SuccessStatus.OK;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    // MEMBER
    FOUND_MEMBER(OK, "MEMBER-200", "Found member"),
    CREATE_MEMBER(CREATED, "MEMBER-201", "Create member"),

    // FOLLOW
    FOUND_FOLLOWER(OK, "FOLLOWER-200", "Found follower"),
    CREATE_FOLLOWER(CREATED, "FOLLOWER-201", "Create follower"),

    // POST
    FOUND_POST(OK, "Post-200", "Found post"),
    CREATE_POST(CREATED, "POST-201", "Create post"),

    // CHATROOM
    FOUND_CHATROOM(OK, "CHATROOM-200", "Found chatroom"),
    CREATE_CHATROOM(CREATED, "CHATROOM-201", "Create chatroom");

    private final SuccessStatus status;
    private final String code;
    private final String message;
}
