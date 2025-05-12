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

    CREATE_FOLLOWER(CREATED, "FOLLOWER-200", "Create follower"),
    DELETE_FOLLOWING(OK, "FOLLOWER-201", "Delete following"),
    FOUND_FOLLOWS(OK, "FOLLOWER-202", "Found follows"),
    COUNT_FOLLOWS(OK, "FOLLOWER-203", "Count follows");

    private final SuccessStatus status;
    private final String code;
    private final String message;
}
