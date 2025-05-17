package io.twogether.nbe_5_7_2_02team.post.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

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
        private Long numLikes;
        private Long chatRoomId;
        private Long memberId;
        private String memberName;
        private String memberImage;
        private List<String> tags;

        @QueryProjection
        public PostGetResult(Post post, Long numLikes, Long chatRoomId, List<String> tags) {
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
            this.numLikes = numLikes;
            this.tags = tags;
        }
    }
}
