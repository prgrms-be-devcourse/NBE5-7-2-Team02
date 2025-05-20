package io.twogether.nbe_5_7_2_02team.member.dto.response;

import io.twogether.nbe_5_7_2_02team.member.domain.Member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberUpdateResponse {

    private Long id;
    private String name;
    private String profileImage;
    private Long followerCount;
    private Long followingCount;
    private boolean following; // 본인이라면 false
    private boolean owner; // 본인 여부

    public static MemberUpdateResponse of(Member member, long followerCount, long followingCount) {
        return MemberUpdateResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .profileImage(member.getProfileImage())
                .followerCount(followerCount)
                .followingCount(followingCount)
                .following(false)
                .owner(true)
                .build();
    }
}
