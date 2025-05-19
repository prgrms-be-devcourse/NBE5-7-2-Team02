package io.twogether.nbe_5_7_2_02team.oauth.jwt;

import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.MemberDetails;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.TokenBody;
import io.twogether.nbe_5_7_2_02team.oauth.service.OAuthService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final OAuthService oAuthService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String token = resolveToken(request);

        try {
            if (token != null && jwtTokenProvider.validate(token)) {

                TokenBody tokenBody = jwtTokenProvider.parseJwt(token);
                MemberDetails memberDetails =
                        oAuthService.getMemberDetailsById(tokenBody.getMemberId());

                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(
                                memberDetails, token, memberDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ErrorException ex) {
            // 예외 코드를 RestAuthenticationEntryPoint로 넘길 수 있도록 설정
            request.setAttribute("exception", ex.getErrorCode());
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
