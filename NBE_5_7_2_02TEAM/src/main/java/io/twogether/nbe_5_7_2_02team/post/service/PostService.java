package io.twogether.nbe_5_7_2_02team.post.service;

import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.post.dao.PostRepository;
import io.twogether.nbe_5_7_2_02team.post.dao.PostTagRepository;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;
import io.twogether.nbe_5_7_2_02team.post.domain.PostTag;
import io.twogether.nbe_5_7_2_02team.post.dto.request.PostCreateRequest;
import io.twogether.nbe_5_7_2_02team.post.dto.response.PostCreateResponse;
import io.twogether.nbe_5_7_2_02team.post.util.ImageUploader;
import io.twogether.nbe_5_7_2_02team.post.util.PostMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final MemberRepository memberRepository;
    private final PostMapper postMapper;
    private final ImageUploader imageUploader;

    @Transactional
    public PostCreateResponse createPost(PostCreateRequest request, Long memberId) {

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_MEMBER));

        Post post = postMapper.toEntity(request, member);
        postRepository.save(post);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<String> savedPaths = imageUploader.saveImages(request.getImages(), post.getId());
            post.setImageUrls(savedPaths);
        }

        List<PostTag> postTags = postMapper.toPostTags(post, request.getTags());
        postTagRepository.saveAll(postTags);

        return new PostCreateResponse(post.getId());
    }

}
