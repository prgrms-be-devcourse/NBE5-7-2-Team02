package io.twogether.nbe_5_7_2_02team.oauth.dao;

import io.twogether.nbe_5_7_2_02team.oauth.domain.RefreshTokenBlackList;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenBlackListRepository
        extends JpaRepository<RefreshTokenBlackList, Long> {}
