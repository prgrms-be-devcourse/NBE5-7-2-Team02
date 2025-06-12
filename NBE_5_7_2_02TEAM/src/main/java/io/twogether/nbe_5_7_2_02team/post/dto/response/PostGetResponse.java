package io.twogether.nbe_5_7_2_02team.post.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import io.twogether.nbe_5_7_2_02team.post.domain.Post;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Getter
public class PostGetResponse {

    private List<PostGetResult> posts;

    public static PostGetResponse from(List<PostGetResult> response) {
        PostGetResponse postGetResponse = new PostGetResponse();
        postGetResponse.posts = response;
        return postGetResponse;
    }

    @Getter
    public static class PostGetResult {
        private Long postId;
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String recruitmentStatus;
        private LocalDate recruitmentDeadline;
        private List<RecruitmentFieldResponse> recruitmentFields;
        private Long numLikes;
        private Long chatRoomId;
        private Long memberId;
        private String memberName;
        private String memberImage;
        private List<String> tags;
        private List<String> images;
        private Boolean isLike;

        @QueryProjection
        public PostGetResult(
                Post post, Long numLikes, Long chatRoomId, List<String> tags, Boolean isLike) {
            this.postId = post.getId();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.memberId = post.getMember().getId();
            this.memberName = post.getMember().getName();
            this.memberImage = post.getMember().getProfileImage();
            this.chatRoomId = chatRoomId;
            this.createdAt = post.getCreatedAt();
            this.updatedAt = post.getUpdatedAt();
            this.recruitmentStatus = post.getRecruitmentStatus().name();
            this.recruitmentDeadline = post.getRecruitmentDeadline();
            this.recruitmentFields =
                    post.getRecruitmentFields().stream()
                            .map(RecruitmentFieldResponse::from)
                            .toList();
            this.numLikes = numLikes;
            this.tags = tags;
            this.images = post.getImageUrls();
            this.isLike = isLike;
        }
    }
}
