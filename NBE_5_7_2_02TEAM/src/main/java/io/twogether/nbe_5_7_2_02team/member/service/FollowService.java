package io.twogether.nbe_5_7_2_02team.member.service;

import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.NOT_DUPLICATION_FOLLOW;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.NOT_FOUND_FOLLOWER;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.NOT_FOUND_FOLLOWING;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.NOT_FOUND_MEMBER;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.NOT_YOURSELF_FOLLOW;

import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.member.dao.FollowRepository;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Follow;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.member.dto.request.FollowRequest;
import io.twogether.nbe_5_7_2_02team.member.dto.response.FollowCreateResponse;
import io.twogether.nbe_5_7_2_02team.member.dto.response.MemberCreateResponse;
import io.twogether.nbe_5_7_2_02team.member.util.mapper.FollowMapper;
import io.twogether.nbe_5_7_2_02team.member.util.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public FollowCreateResponse createFollow(FollowRequest followRequest) {

        Member follower =
                memberRepository
                        .findById(followRequest.getFollowerId())
                        .orElseThrow(() -> new ErrorException(NOT_FOUND_FOLLOWER));
        Member following =
                memberRepository
                        .findById(followRequest.getFollowingId())
                        .orElseThrow(() -> new ErrorException(NOT_FOUND_FOLLOWING));

        if (follower.equals(following)) {
            throw new ErrorException(NOT_YOURSELF_FOLLOW);
        }

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new ErrorException(NOT_DUPLICATION_FOLLOW);
        }

        Follow follow = followRepository.save(new Follow(follower, following));
        Long followerCount = followRepository.countByFollowing(following);
        Long followingCount = followRepository.countByFollower(follower);

        return FollowMapper.toFollowCreateResponse(follow, followerCount, followingCount);
    }

    @Transactional
    public void deleteFollow(FollowRequest followRequest) {
        Member follower =
                memberRepository
                        .findById(followRequest.getFollowerId())
                        .orElseThrow(() -> new ErrorException(NOT_FOUND_FOLLOWER));
        Member following =
                memberRepository
                        .findById(followRequest.getFollowingId())
                        .orElseThrow(() -> new ErrorException(NOT_FOUND_FOLLOWING));

        followRepository.deleteByFollowerAndFollowing(follower, following);
    }

    @Transactional(readOnly = true)
    public Long getFollowerCount(Long memberId) {
        Member member =
                memberRepository
                        .findById(memberId)
                        .orElseThrow(() -> new ErrorException(NOT_FOUND_MEMBER));
        return followRepository.countByFollowing(member);
    }

    @Transactional(readOnly = true)
    public Long getFollowingCount(Long memberId) {
        Member member =
                memberRepository
                        .findById(memberId)
                        .orElseThrow(() -> new ErrorException(NOT_FOUND_MEMBER));
        return followRepository.countByFollower(member);
    }

    @Transactional(readOnly = true)
    public Page<MemberCreateResponse> getFollowers(Long memberId, Pageable pageable) {
        Member member =
                memberRepository
                        .findById(memberId)
                        .orElseThrow(() -> new ErrorException(NOT_FOUND_MEMBER));
        return followRepository
                .findFollowerMembers(member, pageable)
                .map(MemberMapper::toMemberCreateResponse);
    }

    @Transactional(readOnly = true)
    public Page<MemberCreateResponse> getFollowings(Long memberId, Pageable pageable) {
        Member member =
                memberRepository
                        .findById(memberId)
                        .orElseThrow(() -> new ErrorException(NOT_FOUND_MEMBER));
        return followRepository
                .findFollowingMembers(member, pageable)
                .map(MemberMapper::toMemberCreateResponse);
    }
}
