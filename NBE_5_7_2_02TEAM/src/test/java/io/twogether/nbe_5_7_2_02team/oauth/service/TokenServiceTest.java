package io.twogether.nbe_5_7_2_02team.oauth.service;

import static org.junit.jupiter.api.Assertions.*;

import com.github.database.rider.core.api.dataset.DataSet;

import io.twogether.nbe_5_7_2_02team.browser.template.BrowserTestTemplate;
import io.twogether.nbe_5_7_2_02team.global.annotation.FlywayReset;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.oauth.dao.RefreshTokenBlackListRepository;
import io.twogether.nbe_5_7_2_02team.oauth.dao.RefreshTokenRepository;
import io.twogether.nbe_5_7_2_02team.oauth.domain.RefreshToken;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.TokenPair;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@FlywayReset
@Transactional
class TokenServiceTest extends BrowserTestTemplate {

    @Autowired MemberRepository memberRepository;
    @Autowired TokenService tokenService;
    @Autowired RefreshTokenBlackListRepository refreshTokenBlackListRepository;
    @Autowired RefreshTokenRepository refreshTokenRepository;

    @Test
    @DataSet(value = "datasets/v2/member.yml", cleanBefore = true, cleanAfter = true)
    void refresh_success() throws Exception {

        TokenPair originTokenPair = genTokenPair(1L);

        TokenPair refreshTokenPair = tokenService.refreshToken(originTokenPair.getRefreshToken());

        assertEquals(originTokenPair.getRefreshToken(), refreshTokenPair.getRefreshToken());
    }

    @Test
    @DataSet(value = "datasets/v2/member.yml", cleanBefore = true, cleanAfter = true)
    void logout_success() throws Exception {

        TokenPair tokenPair = genTokenPair(1L);

        tokenService.invalidateRefreshToken(tokenPair.getRefreshToken());

        RefreshToken refreshToken = refreshTokenRepository.findByMemberId(1L).orElseThrow();

        assertNotNull(refreshTokenBlackListRepository.findByRefreshTokenId(refreshToken.getId()));
    }

    private TokenPair genTokenPair(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        return jwtTokenProvider.generateTokenPair(member);
    }
}
