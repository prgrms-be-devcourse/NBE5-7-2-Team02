package io.twogether.nbe_5_7_2_02team.member.util.mapper;

import io.twogether.nbe_5_7_2_02team.member.domain.Follow;
import io.twogether.nbe_5_7_2_02team.member.dto.response.FollowCreateResponse;

public class FollowMapper {

    public FollowMapper() {}

    public static FollowCreateResponse toFollowCreateResponse(Follow follow) {
        return new FollowCreateResponse(follow.getFollower().getId(), follow.getFollowing().getId());
    }
}
