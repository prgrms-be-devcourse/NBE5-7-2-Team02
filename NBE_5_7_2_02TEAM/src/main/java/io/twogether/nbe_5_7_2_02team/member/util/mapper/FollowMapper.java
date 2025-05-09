package io.twogether.nbe_5_7_2_02team.member.util.mapper;

import io.twogether.nbe_5_7_2_02team.member.domain.Follow;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.member.dto.FollowCreateResponse;
import io.twogether.nbe_5_7_2_02team.member.dto.MemberCreateResponse;

public class FollowMapper {

    public FollowMapper() {
    }

    public static FollowCreateResponse toFollowCreateResponse(Follow follow) {
        return FollowCreateResponse.builder()
            .followerId(follow.getFollower().getId())
            .followingId(follow.getFollowing().getId())
            .build();
    }

}
