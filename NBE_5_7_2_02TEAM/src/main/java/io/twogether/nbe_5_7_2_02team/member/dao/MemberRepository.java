package io.twogether.nbe_5_7_2_02team.member.dao;

import io.twogether.nbe_5_7_2_02team.member.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
}
