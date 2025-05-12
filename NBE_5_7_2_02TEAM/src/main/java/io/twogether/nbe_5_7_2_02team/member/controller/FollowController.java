package io.twogether.nbe_5_7_2_02team.member.controller;

import static io.twogether.nbe_5_7_2_02team.global.response.success.SuccessCode.*;

import io.twogether.nbe_5_7_2_02team.global.response.success.BaseResponse;
import io.twogether.nbe_5_7_2_02team.global.response.success.SuccessCode;
import io.twogether.nbe_5_7_2_02team.member.dto.FollowCreateResponse;
import io.twogether.nbe_5_7_2_02team.member.dto.FollowRequest;
import io.twogether.nbe_5_7_2_02team.member.dto.MemberCreateResponse;
import io.twogether.nbe_5_7_2_02team.member.service.FollowService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping
    public ResponseEntity<?> follow(@RequestBody FollowRequest followRequest) {
        FollowCreateResponse response = followService.createFollow(followRequest);
        return BaseResponse.of(SuccessCode.CREATE_FOLLOWER, response, URI.create("/api/follow"));
    }

    @DeleteMapping
    public ResponseEntity<?> unfollow(@RequestBody FollowRequest followRequest) {
        followService.deleteFollow(followRequest);
        return BaseResponse.of(DELETE_FOLLOWING, null, null);
    }

    @GetMapping("{memberId}/followers")
    public ResponseEntity<?> getFollowers(@PathVariable Long memberId) {
        List<MemberCreateResponse> followers = followService.getFollowers(memberId);
        return BaseResponse.of(FOUND_FOLLOWS, followers, null);
    }

    @GetMapping("{memberId}/followings")
    public ResponseEntity<?> getFollowings(@PathVariable Long memberId) {
        List<MemberCreateResponse> followings = followService.getFollowings(memberId);
        return BaseResponse.of(FOUND_FOLLOWS, followings, null);
    }

    @GetMapping("{memberId}/follwers/count")
    public ResponseEntity<?> getFollwersCount(@PathVariable Long memberId) {
        Long count = followService.getFollowerCount(memberId);
        return BaseResponse.of(COUNT_FOLLOWS, count, null);
    }

    @GetMapping("{memberId}/follwerings/count")
    public ResponseEntity<?> getFollwingsCount(@PathVariable Long memberId) {
        Long count = followService.getFollowingCount(memberId);
        return BaseResponse.of(COUNT_FOLLOWS, count, null);
    }
}
