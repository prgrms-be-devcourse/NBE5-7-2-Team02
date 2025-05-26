package io.twogether.nbe_5_7_2_02team.oauth.service;

import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.oauth.domain.RefreshToken;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.TokenBody;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.TokenPair;
import io.twogether.nbe_5_7_2_02team.oauth.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    // RefreshToken으로 AccessToken과 RefreshToken 재발급
    @Transactional
    public TokenPair refreshToken(String refreshTokenValue) {

        jwtTokenProvider.refreshValidate(refreshTokenValue);

        TokenBody tokenBody = jwtTokenProvider.parseJwt(refreshTokenValue);
        Long memberId = tokenBody.getMemberId();

        jwtTokenProvider
                .findRefreshToken(memberId)
                .filter(rt -> rt.getRefreshToken().equals(refreshTokenValue))
                .orElseThrow(() -> new ErrorException(ErrorCode.EXPIRED_REFRESH_TOKEN));

        Member member =
                memberRepository
                        .findById(memberId)
                        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_MEMBER));

        return jwtTokenProvider.generateTokenPair(member);
    }

    // RefreshToken을 무효화해서 로그아웃
    @Transactional
    public void invalidateRefreshToken(String refreshTokenValue) {
        jwtTokenProvider.refreshValidate(refreshTokenValue);

        TokenBody tokenBody = jwtTokenProvider.parseJwt(refreshTokenValue);
        Long memberId = tokenBody.getMemberId();

        RefreshToken refreshToken =
                jwtTokenProvider
                        .findRefreshToken(memberId)
                        .filter(rt -> rt.getRefreshToken().equals(refreshTokenValue))
                        .orElseThrow(() -> new ErrorException(ErrorCode.EXPIRED_REFRESH_TOKEN));

        jwtTokenProvider.addBlackList(refreshToken);
    }
}
