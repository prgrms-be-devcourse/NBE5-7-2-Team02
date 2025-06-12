package io.twogether.nbe_5_7_2_02team.oauth.jwt;

import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.oauth.domain.RefreshToken;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.MemberDetails;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.TokenPair;
import io.twogether.nbe_5_7_2_02team.oauth.service.OAuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;

    @Value("${custom.jwt.redirection.base}")
    private String baseUrl;

    private final OAuthService oAuthService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        MemberDetails principal = (MemberDetails) authentication.getPrincipal();
        Member findMember =
                memberRepository
                        .findById(principal.getId())
                        .orElseThrow(() -> new RuntimeException("Member not found"));

        HashMap<String, String> params = new HashMap<>();

        Optional<RefreshToken> refreshTokenOptional =
                jwtTokenProvider.findRefreshToken(principal.getId());

        if (refreshTokenOptional.isEmpty()) {
            TokenPair tokenPair = jwtTokenProvider.generateTokenPair(findMember);
            params.put("access", tokenPair.getAccessToken());
            params.put("refresh", tokenPair.getRefreshToken());
        } else {
            String accessToken =
                    jwtTokenProvider.issueAccessToken(principal.getId(), principal.getRole());
            params.put("access", accessToken);
            params.put("refresh", refreshTokenOptional.get().getRefreshToken());
        }

        String urlStr = genUrlStr(params);
        getRedirectStrategy().sendRedirect(request, response, urlStr);
    }

    private String genUrlStr(HashMap<String, String> params) {
        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("accessToken", params.get("access"))
                .queryParam("refreshToken", params.get("refresh"))
                .build()
                .toUri()
                .toString();
    }
}
