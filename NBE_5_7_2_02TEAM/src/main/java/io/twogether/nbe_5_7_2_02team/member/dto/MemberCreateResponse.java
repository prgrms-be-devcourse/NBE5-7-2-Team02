package io.twogether.nbe_5_7_2_02team.member.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCreateResponse {

    private Long id;
    private String githubId;
    private String name;
    private String profileImage;
    private String job;
    private String course;

    @Builder
    public MemberCreateResponse(Long id, String githubId, String name, String profileImage,
        String job, String course) {
        this.id = id;
        this.githubId = githubId;
        this.name = name;
        this.profileImage = profileImage;
        this.job = job;
        this.course = course;
    }

}
