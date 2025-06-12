package io.twogether.nbe_5_7_2_02team.post.dao;

import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.post.domain.PostApplication;
import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentField;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostApplicationRepository extends JpaRepository<PostApplication, Long> {

    boolean existsByMemberAndField(Member member, RecruitmentField field);
}
