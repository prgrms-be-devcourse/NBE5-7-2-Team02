package io.twogether.nbe_5_7_2_02team.member.dto.response;

import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.member.domain.Role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpResponse {

    private final Long id;
    private final String email;
    private final String name;
    private final String job;
    private final String course;
    private final Role role;

    public static SignUpResponse from(Member member) {
        return SignUpResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .job(member.getJob())
                .course(member.getCourse())
                .role(member.getRole())
                .build();
    }
}
