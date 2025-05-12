package io.twogether.nbe_5_7_2_02team.member.dao;

import io.twogether.nbe_5_7_2_02team.member.domain.Follow;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("SELECT f.follower FROM Follow f where f.following = :member")
    List<Member> findFollowerMembers(@Param("member") Member member);

    @Query("select f.following from Follow f where f.follower = :member")
    List<Member> findFollowingMembers(@Param("member") Member member);

    boolean existsByFollowerAndFollowing(Member follower, Member following);

    Long countByFollower(Member follower);

    Long countByFollowing(Member following);

    void deleteByFollowerAndFollowing(Member follower, Member following);
}
