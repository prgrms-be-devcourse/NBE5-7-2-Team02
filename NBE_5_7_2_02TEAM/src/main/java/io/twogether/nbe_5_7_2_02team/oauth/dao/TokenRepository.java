package io.twogether.nbe_5_7_2_02team.oauth.dao;

import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.oauth.domain.RefreshToken;
import io.twogether.nbe_5_7_2_02team.oauth.domain.RefreshTokenBlackList;

import java.util.Optional;

public interface TokenRepository {

    RefreshToken save(Member member, String token);

    RefreshTokenBlackList addBlackList(RefreshToken refreshToken);

    Optional<RefreshToken> findValidRefToken(Long memberId);
}
