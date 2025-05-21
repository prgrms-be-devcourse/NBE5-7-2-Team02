package io.twogether.nbe_5_7_2_02team.oauth.api;

import io.twogether.nbe_5_7_2_02team.global.response.success.BaseResponse;
import io.twogether.nbe_5_7_2_02team.global.response.success.SuccessCode;
import io.twogether.nbe_5_7_2_02team.member.dto.request.SignUpRequest;
import io.twogether.nbe_5_7_2_02team.member.dto.response.SignUpResponse;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.MemberDetails;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.TokenPair;
import io.twogether.nbe_5_7_2_02team.oauth.dto.request.LogoutRequest;
import io.twogether.nbe_5_7_2_02team.oauth.dto.request.RefreshRequest;
import io.twogether.nbe_5_7_2_02team.oauth.service.OAuthService;
import io.twogether.nbe_5_7_2_02team.oauth.service.TokenService;

import java.net.URI;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TokenController {

    private final TokenService tokenService;
    private final OAuthService oAuthService;

    @PostMapping("/token/refresh")
    public ResponseEntity<BaseResponse<TokenPair>> refresh(@RequestBody RefreshRequest request) {
        TokenPair newToken = tokenService.refreshToken(request.getRefreshToken());
        return BaseResponse.of(SuccessCode.REFRESH_TOKEN, newToken, null);
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(@RequestBody LogoutRequest request) {
        tokenService.invalidateRefreshToken(request.getRefreshToken());
        return BaseResponse.of(SuccessCode.LOGOUT_TOKEN, null, null);
    }

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<SignUpResponse>> signUp(
        @RequestBody SignUpRequest request, @AuthenticationPrincipal MemberDetails memberDetails) {
        SignUpResponse response =
            oAuthService.signup(request, memberDetails.getId());
        return BaseResponse.of(
            SuccessCode.SIGNUP_MEMBER,
            response,
            URI.create("/api/members/" + response.getId()));
    }
}
