package io.twogether.nbe_5_7_2_02team.post.util;

import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;
import io.twogether.nbe_5_7_2_02team.post.domain.PostTag;
import io.twogether.nbe_5_7_2_02team.post.dto.request.PostCreateRequest;
import io.twogether.nbe_5_7_2_02team.post.dto.request.PostUpdateRequest;
import io.twogether.nbe_5_7_2_02team.tag.dao.TagRepository;
import io.twogether.nbe_5_7_2_02team.tag.domain.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PostMapper {

    private final TagRepository tagRepository;

    public Post toEntity(PostCreateRequest request, Member member) {
        return Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .recruitmentStatus(request.getRecruitmentStatus())
                .imageUrls(new ArrayList<>())
                .member(member)
                .build();
    }

    public List<PostTag> toPostTags(Post post, List<String> tags) {

        if (tags == null || tags.isEmpty()) {
            return Collections.emptyList();
        }

        return tags.stream()
                .map(
                        name -> {
                            Tag tag =
                                    tagRepository
                                            .findByName(name)
                                            .orElseGet(
                                                    () ->
                                                            tagRepository.save(
                                                                    Tag.builder()
                                                                            .name(name)
                                                                            .build()));
                            return PostTag.builder().post(post).tag(tag).build();
                        })
                .toList();
    }

    public void updateFromRequest(Post post, PostUpdateRequest request) {
        post.update(
            request.getTitle(),
            request.getContent(),
            request.getRecruitmentStatus()
        );
    }
}
