package io.twogether.nbe_5_7_2_02team.post.service;

import static io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus.DONE;
import static io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus.NONE;
import static io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus.RECRUITING;

import io.twogether.nbe_5_7_2_02team.chat.dao.ChatRepository;
import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.post.dao.LikesRepository;
import io.twogether.nbe_5_7_2_02team.post.dao.PostApplicationRepository;
import io.twogether.nbe_5_7_2_02team.post.dao.PostRepository;
import io.twogether.nbe_5_7_2_02team.post.dao.PostTagRepository;
import io.twogether.nbe_5_7_2_02team.post.domain.Likes;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;
import io.twogether.nbe_5_7_2_02team.post.domain.PostApplication;
import io.twogether.nbe_5_7_2_02team.post.domain.PostTag;
import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentField;
import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus;
import io.twogether.nbe_5_7_2_02team.post.dto.request.PostCreateRequest;
import io.twogether.nbe_5_7_2_02team.post.dto.request.PostGetRequest;
import io.twogether.nbe_5_7_2_02team.post.dto.request.PostUpdateRequest;
import io.twogether.nbe_5_7_2_02team.post.dto.response.PostDetailResponse;
import io.twogether.nbe_5_7_2_02team.post.dto.response.PostGetResponse;
import io.twogether.nbe_5_7_2_02team.post.dto.response.PostResponse;
import io.twogether.nbe_5_7_2_02team.post.util.ImageUploader;
import io.twogether.nbe_5_7_2_02team.post.util.PostMapper;
import io.twogether.nbe_5_7_2_02team.tag.dao.TagRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final MemberRepository memberRepository;
    private final PostMapper postMapper;
    private final ImageUploader imageUploader;
    private final ChatRepository chatRepository;
    private final LikesRepository likesRepository;
    private final PostApplicationRepository postApplicationRepository;
    private final TagRepository tagRepository;

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
        Post updatePost =
                postRepository
                        .findById(postId)
                        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_POST));

        if (!updatePost.getMember().getId().equals(memberId)) {
            throw new ErrorException(ErrorCode.UNAUTHORIZED_POST_ACCESS);
        }

        postMapper.updateFromRequest(updatePost, request);

        if (!CollectionUtils.isEmpty(request.getImages())) {
            imageUploader.deletePostImageByFolder(postId);
            List<String> savedPaths = imageUploader.saveImages(request.getImages(), postId);
            updatePost.setImageUrls(savedPaths);
        }

        postTagRepository.deleteAllByPost(updatePost);
        if (request.getTags() != null) {
            List<PostTag> newTags = postMapper.toPostTags(updatePost, request.getTags());
            postTagRepository.saveAll(newTags);
        }
        deleteUnusedTags();

        if (request.getRecruitmentFields() != null) {
            updatePost.getRecruitmentFields().clear();
            List<RecruitmentField> newFields =
                    postMapper.toRecruitmentFields(updatePost, request.getRecruitmentFields());
            updatePost.getRecruitmentFields().addAll(newFields);
        }

        return new PostResponse(updatePost.getId());
    }

    @Transactional
    public void deletePost(Long postId, Long memberId) {

        Post deletePost =
                postRepository
                        .findById(postId)
                        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_POST));

        if (!deletePost.getMember().getId().equals(memberId)) {
            throw new ErrorException(ErrorCode.UNAUTHORIZED_POST_ACCESS);
        }

        likesRepository.deleteByPost(deletePost);
        chatRepository.deleteByPost(deletePost);
        imageUploader.deletePostImageByFolder(deletePost.getId());
        postRepository.delete(deletePost);
        deleteUnusedTags();
    }

    private void deleteUnusedTags() {
        List<Long> referredTagIds = postTagRepository.findAllTagIds();
        tagRepository.deleteUnusedTags(referredTagIds);
    }

    @Transactional(readOnly = true)
    public PostGetResponse getFilteredPosts(PostGetRequest request, UserDetails userDetails) {
        Long memberId = userDetails != null ? Long.valueOf(userDetails.getUsername()) : null;

        return PostGetResponse.from(
                postRepository.findFilteredPosts(
                        memberId,
                        request.getLastPostId(),
                        request.getLimit(),
                        parseRecruitmentStatus(request.getIsRecruit()),
                        request.getIsFollowing(),
                        request.getTags()));
    }

    @Transactional(readOnly = true)
    public PostGetResponse getPostsByMember(PostGetRequest request, Long memberId) {
        return PostGetResponse.from(
                postRepository.findPostsByMemberId(
                        memberId, request.getLastPostId(), request.getLimit()));
    }

    private RecruitmentStatus parseRecruitmentStatus(Boolean isRecruit) {
        if (isRecruit != null) {
            return isRecruit ? RECRUITING : DONE;
        }
        return NONE;
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPostById(Long postId) {
        Post post =
                postRepository
                        .findById(postId)
                        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_POST));

        List<String> tagNames =
                post.getPostTags().stream().map(tag -> tag.getTag().getName()).toList();

        return new PostDetailResponse(
                post.getTitle(),
                post.getContent(),
                post.getRecruitmentStatus(),
                tagNames,
                post.getImageUrls());
    }

    @Transactional
    public void likePost(Long postId, Long memberId) {

        Member member =
                memberRepository
                        .findById(memberId)
                        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_MEMBER));

        Post post =
                postRepository
                        .findById(postId)
                        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_POST));

        Optional<Likes> existingLike = likesRepository.findByPostAndMember(post, member);
        if (existingLike.isPresent()) {
            throw new ErrorException(ErrorCode.LIKE_ALREADY_EXIST);
        }

        Likes likes = Likes.builder().member(member).post(post).build();
        likesRepository.save(likes);
    }

    @Transactional
    public void unlikePost(Long postId, Long memberId) {

        Member member =
                memberRepository
                        .findById(memberId)
                        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_MEMBER));

        Post post =
                postRepository
                        .findById(postId)
                        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_POST));

        Optional<Likes> likes = likesRepository.findByPostAndMember(post, member);
        if (likes.isPresent()) {
            likesRepository.delete(likes.get());
        } else {
            throw new ErrorException(ErrorCode.NOT_FOUND_LIKE);
        }
    }

    @Transactional
    public void apply(Long postId, String fieldName, Long memberId) {

        Member member =
                memberRepository
                        .findById(memberId)
                        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_MEMBER));

        Post post =
                postRepository
                        .findById(postId)
                        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_POST));

        if (post.getRecruitmentStatus() != RECRUITING) {
            throw new ErrorException(ErrorCode.RECRUITMENT_NOT_AVAILABLE);
        }

        if (post.getMember().getId().equals(memberId)) {
            throw new ErrorException(ErrorCode.CANNOT_APPLY_TO_OWN_POST);
        }

        RecruitmentField field =
                post.getRecruitmentFields().stream()
                        .filter(f -> f.getFieldName().trim().equalsIgnoreCase(fieldName.trim()))
                        .findFirst()
                        .orElseThrow(
                                () -> new ErrorException(ErrorCode.NOT_FOUND_RECRUITMENT_FIELD));

        if (field.isClosed()) {
            throw new ErrorException(ErrorCode.RECRUITMENT_CLOSED);
        }

        boolean alreadyApplied = postApplicationRepository.existsByMemberAndField(member, field);
        if (alreadyApplied) {
            throw new ErrorException(ErrorCode.ALREADY_APPLIED);
        }

        PostApplication application =
                PostApplication.builder().member(member).post(post).field(field).build();
        postApplicationRepository.save(application);

        field.increaseCount();
    }
}
