package io.twogether.nbe_5_7_2_02team.member.api;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.HttpStatus.CREATED;

import io.twogether.nbe_5_7_2_02team.member.dto.request.FollowRequest;
import io.twogether.nbe_5_7_2_02team.member.dto.response.FollowCreateResponse;
import io.twogether.nbe_5_7_2_02team.member.dto.response.MemberCreateResponse;
import io.twogether.nbe_5_7_2_02team.member.service.FollowService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class MyFollowController {

    private final FollowService followService;

    @PostMapping("/{targetId}")
    public ResponseEntity<FollowCreateResponse> follow(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long targetId) {
        FollowRequest followRequest =
                FollowRequest.of(Long.parseLong(userDetails.getUsername()), targetId);
        FollowCreateResponse response = followService.createFollow(followRequest);
        return ResponseEntity.status(CREATED).body(response);
    }

    @DeleteMapping("/{targetId}")
    public ResponseEntity<Void> unfollow(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long targetId) {
        followService.deleteFollow(
                FollowRequest.of(Long.parseLong(userDetails.getUsername()), targetId));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/followers")
    public ResponseEntity<Page<MemberCreateResponse>> getFollowers(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "id", direction = DESC) Pageable pageable) {
        Page<MemberCreateResponse> followers =
                followService.getFollowers(Long.parseLong(userDetails.getUsername()), pageable);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/me/followings")
    public ResponseEntity<Page<MemberCreateResponse>> getFollowings(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "id", direction = DESC) Pageable pageable) {
        Page<MemberCreateResponse> followings =
                followService.getFollowings(Long.parseLong(userDetails.getUsername()), pageable);
        return ResponseEntity.ok(followings);
    }

    @GetMapping("/me/followers/count")
    public ResponseEntity<Long> getFollwersCount(@AuthenticationPrincipal UserDetails userDetails) {
        Long count = followService.getFollowerCount(Long.parseLong(userDetails.getUsername()));
        return ResponseEntity.ok(count);
    }

    @GetMapping("/me/followings/count")
    public ResponseEntity<Long> getFollwingsCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long count = followService.getFollowingCount(Long.parseLong(userDetails.getUsername()));
        return ResponseEntity.ok(count);
    }
}
