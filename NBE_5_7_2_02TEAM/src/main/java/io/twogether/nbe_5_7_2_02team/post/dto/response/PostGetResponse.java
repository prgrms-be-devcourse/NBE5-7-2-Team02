package io.twogether.nbe_5_7_2_02team.post.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostGetResponse {

    private List<PostResponse> posts = new ArrayList<>();

    @Getter
    @Builder
    public static class PostResponse {
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
    }
}
