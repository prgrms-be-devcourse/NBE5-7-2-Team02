package io.twogether.nbe_5_7_2_02team.member.service;

import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.NOT_FOUND_MEMBER;

import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.member.dao.FollowRepository;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.member.dto.request.UpdateProfileRequest;
import io.twogether.nbe_5_7_2_02team.member.dto.response.MyPageResponse;
import io.twogether.nbe_5_7_2_02team.member.util.mapper.MemberMapper;
import io.twogether.nbe_5_7_2_02team.post.dao.PostRepository;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;

    @Transactional(readOnly = true)
    public MyPageResponse getMemberPage(Long targetMemberId, Long viewerId) {
        Member target =
                memberRepository
                        .findById(targetMemberId)
                        .orElseThrow(() -> new ErrorException(NOT_FOUND_MEMBER));
        List<Post> posts = postRepository.findAllByMemberId(targetMemberId);

        Long followerCount = followRepository.countByFollowing(target);
        Long followingCount = followRepository.countByFollower(target);

        // 자기 자신 조회하는 경우에는 false로 고정
        boolean isFollowing = false;

        if (!targetMemberId.equals(viewerId)) {
            isFollowing =
                    followRepository.existsByFollowerAndFollowing(
                            memberRepository
                                    .findById(viewerId)
                                    .orElseThrow(() -> new ErrorException(NOT_FOUND_MEMBER)),
                            target);
        }

        return MemberMapper.toMyPageResponse(
                target, posts, followerCount, followingCount, isFollowing);
    }

    @Transactional
    public void updateProfile(Long memberId, UpdateProfileRequest request) {
        Member member =
                memberRepository
                        .findById(memberId)
                        .orElseThrow(() -> new ErrorException(NOT_FOUND_MEMBER));
        member.updateProfile(request.getProfileImage());
    }
}
