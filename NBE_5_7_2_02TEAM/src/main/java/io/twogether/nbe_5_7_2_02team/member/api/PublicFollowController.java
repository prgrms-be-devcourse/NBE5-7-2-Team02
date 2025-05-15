package io.twogether.nbe_5_7_2_02team.member.api;

import static io.twogether.nbe_5_7_2_02team.global.response.success.SuccessCode.COUNT_FOLLOWS;
import static io.twogether.nbe_5_7_2_02team.global.response.success.SuccessCode.FOUND_FOLLOWS;
import static org.springframework.data.domain.Sort.Direction.DESC;

import io.twogether.nbe_5_7_2_02team.global.response.success.BaseResponse;
import io.twogether.nbe_5_7_2_02team.member.dto.response.MemberCreateResponse;
import io.twogether.nbe_5_7_2_02team.member.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/follow/public")
@RequiredArgsConstructor
public class PublicFollowController {

    private final FollowService followService;

    @GetMapping("/{memberId}/followers")
    public ResponseEntity<BaseResponse<Page<MemberCreateResponse>>> getFollowers(@PathVariable Long memberId,
        @PageableDefault(size = 20, sort = "id", direction = DESC) Pageable pageable) {
        Page<MemberCreateResponse> followers = followService.getFollowers(memberId, pageable);
        return BaseResponse.of(FOUND_FOLLOWS, followers, null);
    }

    @GetMapping("/{memberId}/followings")
    public ResponseEntity<BaseResponse<Page<MemberCreateResponse>>> getFollowings(@PathVariable Long memberId,
        @PageableDefault(size = 20, sort = "id", direction = DESC) Pageable pageable) {
        Page<MemberCreateResponse> followings = followService.getFollowings(memberId, pageable);
        return BaseResponse.of(FOUND_FOLLOWS, followings, null);
    }

    @GetMapping("/{memberId}/followers/count")
    public ResponseEntity<BaseResponse<Long>> getFollwersCount(@PathVariable Long memberId) {
        Long count = followService.getFollowerCount(memberId);
        return BaseResponse.of(COUNT_FOLLOWS, count, null);
    }

    @GetMapping("/{memberId}/followerings/count")
    public ResponseEntity<BaseResponse<Long>> getFollwingsCount(@PathVariable Long memberId) {
        Long count = followService.getFollowingCount(memberId);
        return BaseResponse.of(COUNT_FOLLOWS, count, null);
    }
}
