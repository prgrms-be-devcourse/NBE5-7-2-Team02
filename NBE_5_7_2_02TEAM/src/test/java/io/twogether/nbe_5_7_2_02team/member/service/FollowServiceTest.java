package io.twogether.nbe_5_7_2_02team.member.service;

import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.NOT_DUPLICATION_FOLLOW;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.NOT_YOURSELF_FOLLOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.member.dto.FollowCreateResponse;
import io.twogether.nbe_5_7_2_02team.member.dto.FollowRequest;
import io.twogether.nbe_5_7_2_02team.member.dto.MemberCreateResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class FollowServiceTest {

    @Autowired
    private FollowService followService;
    @Autowired
    private MemberRepository memberRepository;

    private Member follower1;
    private Member follower2;
    private Member following1;
    private Member following2;

    @BeforeEach
    void setUp() {
        follower1 = memberRepository.save(
            Member.builder()
                .githubId("ghFollower")
                .name("홍길동")
                .profileImage("follower.png")
                .job("Backend")
                .course("3기")
                .build());

        follower2 = memberRepository.save(
            Member.builder()
                .githubId("ghFollower2")
                .name("홍길동")
                .profileImage("follower.png")
                .job("Backend")
                .course("3기")
                .build());

        following1 = memberRepository.save(
            Member.builder()
                .githubId("ghFollowing")
                .name("임꺽정")
                .profileImage("following.png")
                .job("Frontend")
                .course("2기")
                .build());

        following2 = memberRepository.save(
            Member.builder()
                .githubId("ghFollowing2")
                .name("임꺽정")
                .profileImage("following.png")
                .job("Frontend")
                .course("2기")
                .build());
    }

    @Test
    @DisplayName("팔로우 생성 성공")
    void testCreateFollow() {
        FollowRequest request = new FollowRequest(follower1.getId(), following1.getId());

        FollowCreateResponse response = followService.createFollow(request);

        assertThat(response.getFollowerId()).isEqualTo(follower1.getId());
        assertThat(response.getFollowingId()).isEqualTo(following1.getId());
    }

    @Test
    @DisplayName("자기 자신은 팔로우할 수 없다")
    void testCreateFollow_selfFollow_throwsException() {
        FollowRequest request = new FollowRequest(follower1.getId(), follower1.getId());

        ErrorException e = assertThrows(ErrorException.class, () -> followService.createFollow(request));

        assertThat(e.getErrorCode()).isEqualTo(NOT_YOURSELF_FOLLOW);

    }

    @Test
    @DisplayName("중복 팔로우는 불가능하다")
    void testCreateFollow_duplicateFollow_throwsException() {
        FollowRequest request = new FollowRequest(follower1.getId(), following1.getId());
        FollowRequest request2 = new FollowRequest(follower1.getId(), following1.getId());
        followService.createFollow(request);

        ErrorException e = assertThrows(ErrorException.class, () -> followService.createFollow(request2));

        assertThat(e.getErrorCode()).isEqualTo(NOT_DUPLICATION_FOLLOW);
    }

    @Test
    @DisplayName("팔로우 삭제 성공")
    void testDeleteFollow() {
        FollowRequest request = new FollowRequest(follower1.getId(), following1.getId());
        followService.createFollow(request);
        followService.deleteFollow(request);
    }

    @Test
    @DisplayName("팔로워 수 조회")
    void testGetFollowerCount() {
        followService.createFollow(new FollowRequest(follower1.getId(), following1.getId()));
        followService.createFollow(new FollowRequest(follower2.getId(), following1.getId()));
        Long count = followService.getFollowerCount(following1.getId());

        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("팔로잉 수 조회")
    void testGetFollowingCount() {
        followService.createFollow(new FollowRequest(follower1.getId(), following1.getId()));
        followService.createFollow(new FollowRequest(follower1.getId(), following2.getId()));
        Long count = followService.getFollowingCount(follower1.getId());

        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("팔로워 목록 조회")
    void testGetFollowers() {
        followService.createFollow(new FollowRequest(follower1.getId(), following1.getId()));
        followService.createFollow(new FollowRequest(follower2.getId(), following1.getId()));

        List<MemberCreateResponse> followers = followService.getFollowers(following1.getId());

        assertThat(followers).hasSize(2);
        assertThat(followers.get(0).getGithubId()).isEqualTo("ghFollower");
        assertThat(followers.get(1).getGithubId()).isEqualTo("ghFollower2");
    }

    @Test
    @DisplayName("팔로잉 목록 조회")
    void testGetFollowings() {
        followService.createFollow(new FollowRequest(follower1.getId(), following1.getId()));
        followService.createFollow(new FollowRequest(follower1.getId(), following2.getId()));

        List<MemberCreateResponse> followings = followService.getFollowings(follower1.getId());

        assertThat(followings).hasSize(2);
        assertThat(followings.get(0).getGithubId()).isEqualTo("ghFollowing");
        assertThat(followings.get(1).getGithubId()).isEqualTo("ghFollowing2");
    }
}