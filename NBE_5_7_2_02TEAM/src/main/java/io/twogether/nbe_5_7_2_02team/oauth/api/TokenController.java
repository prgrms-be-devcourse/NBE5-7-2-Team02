package io.twogether.nbe_5_7_2_02team.oauth.api;

import static org.springframework.http.HttpStatus.CREATED;

import io.twogether.nbe_5_7_2_02team.member.dto.request.SignUpRequest;
import io.twogether.nbe_5_7_2_02team.member.dto.response.SignUpResponse;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.MemberDetails;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.TokenPair;
import io.twogether.nbe_5_7_2_02team.oauth.dto.request.LogoutRequest;
import io.twogether.nbe_5_7_2_02team.oauth.dto.request.RefreshRequest;
import io.twogether.nbe_5_7_2_02team.oauth.service.OAuthService;
import io.twogether.nbe_5_7_2_02team.oauth.service.TokenService;

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
    public ResponseEntity<TokenPair> refresh(@RequestBody RefreshRequest request) {
        TokenPair newToken = tokenService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(newToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {
        tokenService.invalidateRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(
            @RequestBody SignUpRequest request,
            @AuthenticationPrincipal MemberDetails memberDetails) {
        SignUpResponse response = oAuthService.signup(request, memberDetails.getId());
        return ResponseEntity.status(CREATED).body(response);
    }
}
