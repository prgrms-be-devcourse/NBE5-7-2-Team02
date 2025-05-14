package io.twogether.nbe_5_7_2_02team.post.api;

import io.twogether.nbe_5_7_2_02team.global.response.success.BaseResponse;
import io.twogether.nbe_5_7_2_02team.global.response.success.SuccessCode;
import io.twogether.nbe_5_7_2_02team.post.dto.request.PostCreateRequest;
import io.twogether.nbe_5_7_2_02team.post.dto.response.PostCreateResponse;
import io.twogether.nbe_5_7_2_02team.post.service.PostService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @PostMapping(value = "/{memberId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<PostCreateResponse>> createPost(
            @PathVariable Long memberId, @Valid @ModelAttribute PostCreateRequest request) {

        PostCreateResponse response = postService.createPost(request, memberId);
        return BaseResponse.of(
                SuccessCode.CREATE_POST, response, URI.create("/api/posts/" + response.getId()));
    }

    @GetMapping
    public ResponseEntity<?> findPosts(){
        return BaseResponse.of(SuccessCode.NO_CONTENT_POST, null, null);
    }
}
