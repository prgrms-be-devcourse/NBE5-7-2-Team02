package io.twogether.nbe_5_7_2_02team.member.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowRequest {

    private Long followerId;
    private Long followingId;

    @JsonCreator
    public FollowRequest(
        @JsonProperty("followerId") Long followerId,
        @JsonProperty("followingId") Long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }

}
