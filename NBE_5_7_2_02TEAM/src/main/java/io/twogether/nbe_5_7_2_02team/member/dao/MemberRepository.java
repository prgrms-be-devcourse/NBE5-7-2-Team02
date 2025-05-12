package io.twogether.nbe_5_7_2_02team.member.dao;

import io.twogether.nbe_5_7_2_02team.member.domain.Member;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByGithubId(String githubId);
}
