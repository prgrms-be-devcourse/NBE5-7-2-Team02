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

    GITHUB_CALLBACK_SUCCESS(OK, "OAUTH-200", "GitHub access token received successfully"),
    GITHUB_LOGIN_SUCCESS(OK, "OAUTH-201", "GitHub login successful"),
    SIGNUP_SUCCESS(CREATED, "OAUTH-202", "Signup successful");
    ;

    private final SuccessStatus status;
    private final String code;
    private final String message;
}
