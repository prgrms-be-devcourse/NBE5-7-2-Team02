package io.twogether.nbe_5_7_2_02team.post.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostGetRequest {
    private Long lastPostId;
    private Integer limit;
    private Boolean isRecruit;
    private Boolean isFollowing;
    private List<String> tags;
}
