package io.twogether.nbe_5_7_2_02team.post.dao;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import io.twogether.nbe_5_7_2_02team.member.domain.QFollow;
import io.twogether.nbe_5_7_2_02team.post.domain.QLikes;
import io.twogether.nbe_5_7_2_02team.post.domain.QPost;
import io.twogether.nbe_5_7_2_02team.post.domain.QPostTag;
import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus;
import io.twogether.nbe_5_7_2_02team.post.dto.common.PostGetResult;
import io.twogether.nbe_5_7_2_02team.tag.domain.QTag;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepositoryFilterImpl implements PostRepositoryFilter {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PostGetResult> findFilteredPosts(
            Long memberId,
            Long lastPostId,
            Integer limit,
            RecruitmentStatus recruitmentStatus,
            Boolean isFollowing,
            List<String> tags) {
        QPost post = QPost.post;
        QLikes likes = QLikes.likes;

        return queryFactory
                .select(
                        Projections.constructor(
                                PostGetResult.class,
                                post,
                                likes.count().coalesce(0L).as("likeCount")))
                .from(post)
                .leftJoin(likes)
                .on(likes.post.eq(post))
                .where(
                        lastPostIdCondition(post, lastPostId),
                        tagsCondition(post, tags),
                        followingCondition(post, memberId, isFollowing),
                        post.recruitmentStatus.eq(recruitmentStatus))
                .groupBy(post.id)
                .orderBy(post.createdAt.desc())
                .limit(limit)
                .fetch();
    }

    private BooleanExpression lastPostIdCondition(QPost post, Long lastPostId) {
        if (lastPostId == null) return null;

        LocalDateTime lastCreatedAt =
                queryFactory
                        .select(QPost.post.createdAt)
                        .from(QPost.post)
                        .where(QPost.post.id.eq(lastPostId))
                        .fetchOne();

        return lastCreatedAt != null ? post.createdAt.lt(lastCreatedAt) : null;
    }

    private BooleanExpression followingCondition(QPost post, Long memberId, Boolean isFollowing) {
        if (Boolean.TRUE.equals(isFollowing) && memberId != null) {
            return post.member.id.in(
                    queryFactory
                            .select(QFollow.follow.following.id)
                            .from(QFollow.follow)
                            .where(QFollow.follow.follower.id.eq(memberId)));
        }

        return null;
    }

    private BooleanExpression tagsCondition(QPost post, List<String> tags) {
        if (CollectionUtils.isEmpty(tags)) return null;
        BooleanExpression condition = Expressions.asBoolean(true).isTrue();
        for (String tag : tags) {
            condition =
                    condition.and(
                            JPAExpressions.selectOne()
                                    .from(post.postTags, QPostTag.postTag)
                                    .join(QPostTag.postTag.tag, QTag.tag)
                                    .where(QPostTag.postTag.post.eq(post), QTag.tag.name.eq(tag))
                                    .exists());
        }
        return condition;
    }
}
