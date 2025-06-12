package io.twogether.nbe_5_7_2_02team.post.dao;

import io.twogether.nbe_5_7_2_02team.post.domain.Post;
import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryFilter {

    @Query("SELECT p FROM Post p WHERE p.member.id = :memberId")
    List<Post> findAllByMemberId(@Param("memberId") Long memberId);

    List<Post> findByRecruitmentStatusAndRecruitmentDeadlineBefore(
            RecruitmentStatus status, LocalDate deadline);
}
