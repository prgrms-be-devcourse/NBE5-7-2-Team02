package io.twogether.nbe_5_7_2_02team.member.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FollowCreateResponse {

    private Long followerId;
    private Long followingId;
    private Long updatedFollowerCount;
    private Long updatedFollowingCount;
}
