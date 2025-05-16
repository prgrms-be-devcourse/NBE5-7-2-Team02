package io.twogether.nbe_5_7_2_02team.post.api;

import io.twogether.nbe_5_7_2_02team.global.response.success.BaseResponse;
import io.twogether.nbe_5_7_2_02team.global.response.success.SuccessCode;
import io.twogether.nbe_5_7_2_02team.post.dto.request.PostCreateRequest;
import io.twogether.nbe_5_7_2_02team.post.dto.request.PostGetRequest;
import io.twogether.nbe_5_7_2_02team.post.dto.request.PostUpdateRequest;
import io.twogether.nbe_5_7_2_02team.post.dto.response.PostGetResponse;
import io.twogether.nbe_5_7_2_02team.post.dto.response.PostResponse;
import io.twogether.nbe_5_7_2_02team.post.service.PostService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @PostMapping(value = "/{memberId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<PostResponse>> createPost(
            @PathVariable Long memberId, @Valid @ModelAttribute PostCreateRequest request) {

        PostResponse response = postService.createPost(request, memberId);
        return BaseResponse.of(
                SuccessCode.CREATE_POST, response, URI.create("/api/posts/" + response.getId()));
    }

    @PatchMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<PostResponse>> updatePost(
            @PathVariable Long postId,
            @ModelAttribute PostUpdateRequest request,
            @RequestParam Long memberId) {

        PostResponse response = postService.updatePost(postId, request, memberId);
        return BaseResponse.of(SuccessCode.UPDATE_POST, response, null);
    }

    @DeleteMapping(value = "/{postId}")
    public ResponseEntity<BaseResponse<Void>> deletePost(
            @PathVariable Long postId, @RequestParam Long memberId) {

        postService.deletePost(postId, memberId);
        return BaseResponse.of(SuccessCode.DELETE_POST, null, null);
    }

    @GetMapping
    public ResponseEntity<?> findFilteredPosts(
            PostGetRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        PostGetResponse response = postService.getFilteredPosts(request, userDetails);
        if (CollectionUtils.isEmpty(response.getPosts())) {
            return BaseResponse.of(SuccessCode.NO_CONTENT_POST, null, null);
        }
        return BaseResponse.of(SuccessCode.FOUND_POST, response, null);
    }
}
