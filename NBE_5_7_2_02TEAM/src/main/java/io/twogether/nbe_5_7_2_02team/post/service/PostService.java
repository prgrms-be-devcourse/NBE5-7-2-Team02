package io.twogether.nbe_5_7_2_02team.post.service;

import io.twogether.nbe_5_7_2_02team.chat.dao.ChatRoomRepository;
import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.post.dao.PostRepository;
import io.twogether.nbe_5_7_2_02team.post.dao.PostTagRepository;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;
import io.twogether.nbe_5_7_2_02team.post.domain.PostTag;
import io.twogether.nbe_5_7_2_02team.post.dto.request.PostCreateRequest;
import io.twogether.nbe_5_7_2_02team.post.dto.request.PostUpdateRequest;
import io.twogether.nbe_5_7_2_02team.post.dto.response.PostResponse;
import io.twogether.nbe_5_7_2_02team.post.util.ImageUploader;
import io.twogether.nbe_5_7_2_02team.post.util.PostMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final MemberRepository memberRepository;
    private final PostMapper postMapper;
    private final ImageUploader imageUploader;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public PostResponse createPost(PostCreateRequest request, Long memberId) {

        Member member =
            memberRepository
                .findById(memberId)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_MEMBER));

        Post post = postMapper.toEntity(request, member);
        postRepository.save(post);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            List<String> savedPaths = imageUploader.saveImages(request.getImages(), post.getId());
            post.setImageUrls(savedPaths);
        }

        List<PostTag> postTags = postMapper.toPostTags(post, request.getTags());
        postTagRepository.saveAll(postTags);

        return new PostResponse(post.getId());
    }

    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest request, Long memberId) {

        Post updatePost = postRepository.findById(postId)
            .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_POST));

        if (!updatePost.getMember().getId().equals(memberId)) {
            throw new ErrorException(ErrorCode.UNAUTHORIZED_POST_ACCESS);
        }

        postMapper.updateFromRequest(updatePost, request);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            try {
                imageUploader.deletePostImageByFolder(postId);

                List<String> savedPaths = imageUploader.saveImages(request.getImages(), postId);
                updatePost.setImageUrls(savedPaths);
            } catch (Exception e) {
                throw new ErrorException(ErrorCode.IMAGE_UPLOAD_FAILED);
            }
        }

        if (request.getTags() != null) {
            postTagRepository.deleteAllByPost(updatePost);

            List<PostTag> newTags = postMapper.toPostTags(updatePost, request.getTags());
            postTagRepository.saveAll(newTags);
        }
        return new PostResponse(updatePost.getId());
    }

    @Transactional
    public void deletePost(Long postId, Long memberId) {

        Post deletePost = postRepository.findById(postId)
            .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_POST));

        if (!deletePost.getMember().getId().equals(memberId)) {
            throw new ErrorException(ErrorCode.UNAUTHORIZED_POST_ACCESS);
        }

        // TODO: 연결된 좋아요 삭제
        chatRoomRepository.deleteByPost(deletePost);
        imageUploader.deletePostImageByFolder(deletePost.getId());
        postRepository.delete(deletePost);
    }
}
