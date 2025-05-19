package io.twogether.nbe_5_7_2_02team.member.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FollowRequest {

    private Long followerId;
    private Long followingId;

    public static FollowRequest of(Long followerId, Long followingId) {
        return new FollowRequest(followerId, followingId);
    }
}
