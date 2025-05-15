package io.twogether.nbe_5_7_2_02team.oauth.api;

import io.twogether.nbe_5_7_2_02team.global.response.success.BaseResponse;
import io.twogether.nbe_5_7_2_02team.global.response.success.SuccessCode;
import io.twogether.nbe_5_7_2_02team.member.dto.request.SignUpRequest;
import io.twogether.nbe_5_7_2_02team.member.dto.response.LoginResponse;
import io.twogether.nbe_5_7_2_02team.member.dto.response.SignUpResponse;
import io.twogether.nbe_5_7_2_02team.oauth.dto.request.GithubLoginRequest;
import io.twogether.nbe_5_7_2_02team.oauth.dto.response.GitHubLoginResponse;
import io.twogether.nbe_5_7_2_02team.oauth.service.OAuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/oauth2/callback/github")
    public ResponseEntity<BaseResponse<GitHubLoginResponse>> githubCallback(
            @RequestParam String code) {
        GitHubLoginResponse response = oAuthService.getAccessToken(code);
        return BaseResponse.of(SuccessCode.GITHUB_CALLBACK_SUCCESS, response, null);
    }

    @PostMapping("/oauth2/login/github")
    public ResponseEntity<BaseResponse<LoginResponse>> githubLogin(
            @RequestBody GithubLoginRequest request) {
        LoginResponse response = oAuthService.login(request.getAccessToken());
        return BaseResponse.of(SuccessCode.GITHUB_LOGIN_SUCCESS, response, null);
    }

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<SignUpResponse>> signUp(
            @RequestBody SignUpRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        SignUpResponse response =
                oAuthService.signup(request, Long.parseLong(userDetails.getUsername()));
        return BaseResponse.of(
                SuccessCode.SIGNUP_SUCCESS,
                response,
                URI.create("/api/members/" + response.getId()));
    }
}
