package io.twogether.nbe_5_7_2_02team.member.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyPageResponse {

    private Long id;
    private String email;
    private String name;
    private String job;
    private String course;
    private String profileImage;
    private List<PostSummary> posts;

    private Long followerCount;
    private Long followingCount;

    private boolean following;
    private boolean owner;

    @Getter
    @Builder
    public static class PostSummary {
        private Long postId;
        private String title;
    }
}
