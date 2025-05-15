package io.twogether.nbe_5_7_2_02team.member.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCreateResponse {

    private Long id;
    private String email;
    private String name;
    private String profileImage;
    private String job;
    private String course;
    private String githubId;

    public MemberCreateResponse(
            Long id,
            String email,
            String name,
            String profileImage,
            String job,
            String course,
            String githubId) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.profileImage = profileImage;
        this.job = job;
        this.course = course;
        this.githubId = githubId;
    }
}
