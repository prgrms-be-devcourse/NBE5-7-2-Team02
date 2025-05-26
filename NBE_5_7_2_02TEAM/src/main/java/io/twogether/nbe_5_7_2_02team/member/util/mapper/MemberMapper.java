package io.twogether.nbe_5_7_2_02team.member.util.mapper;

import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import io.twogether.nbe_5_7_2_02team.member.dto.response.MemberCreateResponse;
import io.twogether.nbe_5_7_2_02team.member.dto.response.MyPageResponse;
import io.twogether.nbe_5_7_2_02team.member.dto.response.MyPageResponse.PostSummary;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;

import java.util.List;

public class MemberMapper {

    public MemberMapper() {}

    public static MemberCreateResponse toMemberCreateResponse(Member member) {
        return MemberCreateResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .profileImage(member.getProfileImage())
                .job(member.getJob())
                .course(member.getCourse())
                .githubId(member.getGithubId())
                .build();
    }

    public static MyPageResponse toMyPageResponse(
            Member member,
            List<Post> posts,
            Long followerCount,
            Long followingCount,
            boolean following,
            boolean owner) {

        List<PostSummary> postSummaries =
                posts.stream()
                        .map(
                                post ->
                                        MyPageResponse.PostSummary.builder()
                                                .postId(post.getId())
                                                .title(post.getTitle())
                                                .build())
                        .toList();

        return MyPageResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .job(member.getJob())
                .course(member.getCourse())
                .profileImage(member.getProfileImage())
                .posts(postSummaries)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .following(following)
                .owner(owner)
                .build();
    }
}
