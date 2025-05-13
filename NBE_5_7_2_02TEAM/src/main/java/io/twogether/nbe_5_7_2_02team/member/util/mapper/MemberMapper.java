package io.twogether.nbe_5_7_2_02team.member.util.mapper;

import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.member.dto.MemberCreateResponse;

public class MemberMapper {

    public MemberMapper() {}

    public static MemberCreateResponse toMemberCreateResponse(Member member) {
        return MemberCreateResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .profileImage(member.getProfileImage())
                .job(member.getJob())
                .course(member.getCourse())
                .build();
    }
}
