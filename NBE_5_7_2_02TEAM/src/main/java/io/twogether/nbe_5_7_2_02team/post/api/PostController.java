package io.twogether.nbe_5_7_2_02team.post.api;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.twogether.nbe_5_7_2_02team.post.dto.request.PostApplyRequest;
import io.twogether.nbe_5_7_2_02team.post.dto.request.PostCreateRequest;
import io.twogether.nbe_5_7_2_02team.post.dto.request.PostGetRequest;
import io.twogether.nbe_5_7_2_02team.post.dto.request.PostUpdateRequest;
import io.twogether.nbe_5_7_2_02team.post.dto.request.RecruitmentFieldRequest;
import io.twogether.nbe_5_7_2_02team.post.dto.response.PostDetailResponse;
import io.twogether.nbe_5_7_2_02team.post.dto.response.PostGetResponse;
import io.twogether.nbe_5_7_2_02team.post.dto.response.PostResponse;
import io.twogether.nbe_5_7_2_02team.post.service.PostService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute @Valid PostCreateRequest request) {

        if (request.getRecruitmentFieldsJson() != null
                && !request.getRecruitmentFieldsJson().isBlank()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<RecruitmentFieldRequest> fields =
                        Arrays.asList(
                                mapper.readValue(
                                        request.getRecruitmentFieldsJson(),
                                        RecruitmentFieldRequest[].class));
                request.setRecruitmentFields(fields);
            } catch (Exception e) {
                // TODO: 커스텀 Exception으로 변경
                throw new RuntimeException("Invalid recruitmentFieldsJson", e);
            }
        }

        PostResponse response =
                postService.createPost(request, Long.parseLong(userDetails.getUsername()));
        return ResponseEntity.status(CREATED).body(response);
    }

    @PatchMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestPart("post") PostUpdateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal UserDetails userDetails) {

        request.setImages(images);
        PostResponse response =
                postService.updatePost(postId, request, Long.parseLong(userDetails.getUsername()));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {

        postService.deletePost(postId, Long.parseLong(userDetails.getUsername()));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<PostGetResponse> findFilteredPosts(
            @ModelAttribute PostGetRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        PostGetResponse response = postService.getFilteredPosts(request, userDetails);
        if (isEmpty(response.getPosts())) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<PostGetResponse> findPosts(
            @ModelAttribute PostGetRequest request, @PathVariable Long memberId) {
        PostGetResponse response = postService.getPostsByMember(request, memberId);
        if (isEmpty(response.getPosts())) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable Long postId) {
        PostDetailResponse response = postService.getPostById(postId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<Void> likePost(
            @PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {

        postService.likePost(postId, Long.parseLong(userDetails.getUsername()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails) {

        postService.unlikePost(postId, Long.parseLong(userDetails.getUsername()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/apply")
    public ResponseEntity<Void> applyToField(
            @PathVariable Long postId,
            @RequestBody @Valid PostApplyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        postService.apply(
                postId, request.getFieldName(), Long.parseLong(userDetails.getUsername()));
        return ResponseEntity.ok().build();
    }
}
