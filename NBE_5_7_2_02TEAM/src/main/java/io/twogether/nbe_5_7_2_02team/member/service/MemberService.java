package io.twogether.nbe_5_7_2_02team.member.service;

import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.NOT_FOUND_MEMBER;

import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.member.dao.FollowRepository;
import io.twogether.nbe_5_7_2_02team.member.dao.MemberRepository;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.member.dto.request.UpdateProfileRequest;
import io.twogether.nbe_5_7_2_02team.member.dto.response.MemberUpdateResponse;
import io.twogether.nbe_5_7_2_02team.member.dto.response.MyPageResponse;
import io.twogether.nbe_5_7_2_02team.member.util.Uploader.ImageUpload;
import io.twogether.nbe_5_7_2_02team.member.util.mapper.MemberMapper;
import io.twogether.nbe_5_7_2_02team.post.dao.PostRepository;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final FollowRepository followRepository;
    private final ImageUpload imageUpload;

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
        boolean following = false;
        // 자기 자신 조회하는 경우에는 true로 고정
        boolean owner = targetMemberId.equals(viewerId);

        if (!targetMemberId.equals(viewerId)) {
            following =
                    followRepository.existsByFollowerAndFollowing(
                            memberRepository
                                    .findById(viewerId)
                                    .orElseThrow(() -> new ErrorException(NOT_FOUND_MEMBER)),
                            target);
        }

        return MemberMapper.toMyPageResponse(
                target, posts, followerCount, followingCount, following, owner);
    }

    @Transactional
    public MemberUpdateResponse updateProfile(Long memberId, UpdateProfileRequest request) {
        Member member =
                memberRepository
                        .findById(memberId)
                        .orElseThrow(() -> new ErrorException(NOT_FOUND_MEMBER));

        String imageUrl = imageUpload.saveProfileImage(request.getImage(), memberId);

        member.updateProfile(request.getNickname(), imageUrl);

        long followerCount = followRepository.countByFollowing(member);
        long followingCount = followRepository.countByFollower(member);

        return MemberUpdateResponse.of(member, followerCount, followingCount);
    }
}
