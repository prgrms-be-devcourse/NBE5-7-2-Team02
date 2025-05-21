package io.twogether.nbe_5_7_2_02team.global.response.success;

import static io.twogether.nbe_5_7_2_02team.global.response.success.SuccessStatus.CREATED;
import static io.twogether.nbe_5_7_2_02team.global.response.success.SuccessStatus.NO_CONTENT;
import static io.twogether.nbe_5_7_2_02team.global.response.success.SuccessStatus.OK;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    // MEMBER
    FOUND_MEMBER(OK, "MEMBER-200", "Found member"),
    CREATE_MEMBER(CREATED, "MEMBER-201", "Create member"),
    READ_MEMBER(OK, "MEMBER-202", "Read member"),
    UPDATE_MEMBER(OK, "MEMBER-203", "Update member"),
    SIGNUP_MEMBER(CREATED, "MEMBER-204", "Signup member"),


    // JWT
    REFRESH_TOKEN(OK, "JWT-200", "Token refreshed successfully"),
    LOGOUT_TOKEN(OK, "JWT-201", "Logout successful"),

    // FOLLOW
    CREATE_FOLLOWER(CREATED, "FOLLOWER-200", "Create follower"),
    FOUND_FOLLOWER(OK, "FOLLOWER-200", "Found follower"),
    DELETE_FOLLOWING(OK, "FOLLOWER-201", "Delete following"),
    FOUND_FOLLOWS(OK, "FOLLOWER-202", "Found follows"),
    COUNT_FOLLOWS(OK, "FOLLOWER-203", "Count follows"),

    // POST
    FOUND_POST(OK, "POST-200", "Found post"),
    CREATE_POST(CREATED, "POST-201", "Create post"),
    UPDATE_POST(OK, "POST-202", "Update post"),
    DELETE_POST(OK, "POST-203", "Delete post"),
    NO_CONTENT_POST(NO_CONTENT, "POST-204", "No posts to response"),
    LIKE_POST(OK, "POST-205", "Like post"),
    UNLIKE_POST(OK, "POST-206", "Unlike post"),

    // CHAT_MESSAGE
    FOUND_CHAT_MESSAGE(CREATED, "CHAT-MESSAGE-200", "Found chat message"),
    CREATE_CHAT_MESSAGE(OK, "CHAT-MESSAGE-201", "Create chat message"),
    DELETE_CHAT_MESSAGE(OK, "CHAT-MESSAGE-204", "Delete chat message"),

    // CHAT_MEMBER
    FOUND_CHAT_MEMBER(OK, "CHAT-MEMBER-200", "Found chat member"),
    CREATE_CHAT_MEMBER(CREATED, "CHAT-MEMBER-201", "Create chat member"),
    UPDATE_CHAT_MEMBER(OK, "CHAT-MEMBER-202", "Update chat member"),

    // CHATROOM
    FOUND_CHATROOM(OK, "CHATROOM-200", "Found chatroom"),
    CREATE_CHATROOM(CREATED, "CHATROOM-201", "Create chatroom"),

    FOUND_TAG(OK, "TAG-200", "Found tag"),
    NO_CONTENT_TAG(NO_CONTENT, "TAG-204", "No tags to response"),
    ;

    private final SuccessStatus status;
    private final String code;
    private final String message;
}
