package io.twogether.nbe_5_7_2_02team.member.api;

import io.twogether.nbe_5_7_2_02team.member.dto.request.UpdateProfileRequest;
import io.twogether.nbe_5_7_2_02team.member.dto.response.MemberUpdateResponse;
import io.twogether.nbe_5_7_2_02team.member.dto.response.MyPageResponse;
import io.twogether.nbe_5_7_2_02team.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 내 프로필 조회
    @GetMapping("/me")
    public ResponseEntity<MyPageResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        MyPageResponse response =
                memberService.getMemberPage(
                        Long.parseLong(userDetails.getUsername()),
                        Long.parseLong(userDetails.getUsername()));
        return ResponseEntity.ok(response);
    }

    // 내 프로필 수정
    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemberUpdateResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("nickname") String nickname,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        UpdateProfileRequest request = new UpdateProfileRequest(image, nickname);
        Long memberId = Long.parseLong(userDetails.getUsername());

        MemberUpdateResponse response = memberService.updateProfile(memberId, request);
        return ResponseEntity.ok(response);
    }

    // 상대방 프로필 조회
    @GetMapping("/{memberId}")
    public ResponseEntity<MyPageResponse> getOtherProfile(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long memberId) {

        MyPageResponse response =
                memberService.getMemberPage(memberId, Long.parseLong(userDetails.getUsername()));

        return ResponseEntity.ok(response);
    }
}
