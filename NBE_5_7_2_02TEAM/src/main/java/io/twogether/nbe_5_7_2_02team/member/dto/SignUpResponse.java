package io.twogether.nbe_5_7_2_02team.member.dto;

import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.member.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SignUpResponse {

    private final Long id;
    private final String email;
    private final String name;
    private final String job;
    private final String course;
    private final Role role;

    @Builder
    public SignUpResponse(Long id, String email, String name, String job, String course, Role role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.job = job;
        this.course = course;
        this.role = role;
    }

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
