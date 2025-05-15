package io.twogether.nbe_5_7_2_02team.member.api;

import static io.twogether.nbe_5_7_2_02team.global.response.success.SuccessCode.READ_MEMBER;
import static io.twogether.nbe_5_7_2_02team.global.response.success.SuccessCode.UPDATE_MEMBER;

import io.twogether.nbe_5_7_2_02team.global.response.success.BaseResponse;
import io.twogether.nbe_5_7_2_02team.member.dto.request.UpdateProfileRequest;
import io.twogether.nbe_5_7_2_02team.member.dto.response.MyPageResponse;
import io.twogether.nbe_5_7_2_02team.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 내 프로필 조회
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<MyPageResponse>> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        MyPageResponse response =
                memberService.getMemberPage(
                        Long.parseLong(userDetails.getUsername()),
                        Long.parseLong(userDetails.getUsername()));
        return BaseResponse.of(READ_MEMBER, response, null);
    }

    // 내 프로필 수정
    @PatchMapping("/me")
    public ResponseEntity<BaseResponse<Void>> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UpdateProfileRequest request) {

        memberService.updateProfile(Long.parseLong(userDetails.getUsername()), request);
        return BaseResponse.of(UPDATE_MEMBER, null, null);
    }

    // 상대방 프로필 조회
    @GetMapping("/{memberId}")
    public ResponseEntity<BaseResponse<MyPageResponse>> getOtherProfile(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long memberId) {

        MyPageResponse response =
                memberService.getMemberPage(memberId, Long.parseLong(userDetails.getUsername()));

        return BaseResponse.of(READ_MEMBER, response, null);
    }
}
