package io.twogether.nbe_5_7_2_02team.oauth.dao;

import io.twogether.nbe_5_7_2_02team.oauth.domain.RefreshToken;
import io.twogether.nbe_5_7_2_02team.oauth.domain.RefreshTokenBlackList;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenBlackListRepository
        extends JpaRepository<RefreshTokenBlackList, Long> {
    void deleteByRefreshToken(RefreshToken refreshToken);

    Optional<RefreshTokenBlackList> findByRefreshTokenId(Long refreshTokenId);
}
