package io.twogether.nbe_5_7_2_02team.member.dao;

import io.twogether.nbe_5_7_2_02team.member.domain.Member;

import io.twogether.nbe_5_7_2_02team.post.domain.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

}
