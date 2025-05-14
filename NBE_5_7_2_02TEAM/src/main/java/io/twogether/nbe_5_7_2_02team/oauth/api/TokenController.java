package io.twogether.nbe_5_7_2_02team.oauth.api;

import io.twogether.nbe_5_7_2_02team.global.response.success.BaseResponse;
import io.twogether.nbe_5_7_2_02team.global.response.success.SuccessCode;
import io.twogether.nbe_5_7_2_02team.oauth.dto.request.LogoutRequest;
import io.twogether.nbe_5_7_2_02team.oauth.dto.request.RefreshRequest;
import io.twogether.nbe_5_7_2_02team.oauth.dto.TokenPair;
import io.twogether.nbe_5_7_2_02team.oauth.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TokenController {

    private final TokenService tokenService;

    @PostMapping("/token/refresh")
    public ResponseEntity<BaseResponse<TokenPair>> refresh(@RequestBody RefreshRequest request) {
        TokenPair newToken = tokenService.refreshToken(request.getRefreshToken());
        return BaseResponse.of(SuccessCode.REFRESH_TOKEN_SUCCESS, newToken, null);
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(@RequestBody LogoutRequest request) {
        tokenService.invalidateRefreshToken(request.getRefreshToken());
        return BaseResponse.of(SuccessCode.LOGOUT_SUCCESS, null, null);
    }

}
