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

    // OAUTH
    GITHUB_CALLBACK_SUCCESS(OK, "OAUTH-200", "GitHub access token received successfully"),
    GITHUB_LOGIN_SUCCESS(OK, "OAUTH-201", "GitHub login successful"),
    SIGNUP_SUCCESS(CREATED, "OAUTH-202", "Signup successful"),

    // JWT
    REFRESH_TOKEN_SUCCESS(OK, "JWT-200", "Token refreshed successfully"),
    LOGOUT_SUCCESS(OK, "JWT-201", "Logout successful"),

    // FOLLOW
    CREATE_FOLLOWER(CREATED, "FOLLOWER-200", "Create follower"),
    FOUND_FOLLOWER(OK, "FOLLOWER-200", "Found follower"),
    DELETE_FOLLOWING(OK, "FOLLOWER-201", "Delete following"),
    FOUND_FOLLOWS(OK, "FOLLOWER-202", "Found follows"),
    COUNT_FOLLOWS(OK, "FOLLOWER-203", "Count follows"),

    // POST
    FOUND_POST(OK, "POST-200", "Found post"),
    CREATE_POST(CREATED, "POST-201", "Create post"),
    NO_CONTENT_POST(NO_CONTENT, "POST-204", "No posts to response"),

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
