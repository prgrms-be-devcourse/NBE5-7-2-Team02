package io.twogether.nbe_5_7_2_02team.member.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowCreateResponse {

    private Long followerId;
    private Long followingId;

    @Builder
    public FollowCreateResponse(Long followerId, Long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }
}
