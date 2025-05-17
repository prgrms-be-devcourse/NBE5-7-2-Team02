package io.twogether.nbe_5_7_2_02team.post.dto.common;

import io.twogether.nbe_5_7_2_02team.post.domain.Post;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostGetResult {
    private Post post;
    private Long likeCount;
    private List<String> tags;
}
