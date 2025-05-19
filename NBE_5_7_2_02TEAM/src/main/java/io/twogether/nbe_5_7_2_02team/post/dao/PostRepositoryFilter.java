package io.twogether.nbe_5_7_2_02team.post.dao;

import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus;
import io.twogether.nbe_5_7_2_02team.post.dto.response.PostGetResponse.PostGetResult;

import java.util.List;

public interface PostRepositoryFilter {
    List<PostGetResult> findPostsByMemberId(Long memberId, Long lastPostId, Integer limit);

    List<PostGetResult> findFilteredPosts(
            Long memberId,
            Long lastPostId,
            Integer limit,
            RecruitmentStatus recruitmentStatus,
            Boolean isFollowing,
            List<String> tags);
}
