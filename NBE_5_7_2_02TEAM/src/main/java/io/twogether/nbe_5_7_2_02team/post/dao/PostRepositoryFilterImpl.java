package io.twogether.nbe_5_7_2_02team.post.dao;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

import static io.twogether.nbe_5_7_2_02team.chat.domain.QChatRoom.chatRoom;
import static io.twogether.nbe_5_7_2_02team.member.domain.QFollow.follow;
import static io.twogether.nbe_5_7_2_02team.post.domain.QLikes.likes;
import static io.twogether.nbe_5_7_2_02team.post.domain.QPost.post;
import static io.twogether.nbe_5_7_2_02team.post.domain.QPostTag.postTag;
import static io.twogether.nbe_5_7_2_02team.tag.domain.QTag.tag;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import io.twogether.nbe_5_7_2_02team.post.domain.RecruitmentStatus;
import io.twogether.nbe_5_7_2_02team.post.dto.response.PostGetResponse.PostGetResult;
import io.twogether.nbe_5_7_2_02team.post.dto.response.QPostGetResponse_PostGetResult;

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
    public List<PostGetResult> findPostsByMemberId(Long memberId, Long lastPostId, Integer limit) {
        List<Long> limitedPostIds =
                queryFactory
                        .select(post.id)
                        .from(post)
                        .where(lastPostIdCondition(lastPostId), post.member.id.eq(memberId))
                        .orderBy(post.createdAt.desc())
                        .limit(limit)
                        .fetch();

        return getPostResultJoinWithChatRoomAndTag(limitedPostIds, memberId);
    }

    @Override
    public List<PostGetResult> findFilteredPosts(
            Long memberId,
            Long lastPostId,
            Integer limit,
            RecruitmentStatus recruitmentStatus,
            Boolean isFollowing,
            List<String> tags) {

        List<Long> limitedPostIds =
                queryFactory
                        .select(post.id)
                        .from(post)
                        .where(
                                recruitmentStatusCondition(recruitmentStatus),
                                followingCondition(memberId, isFollowing),
                                tagsCondition(tags),
                                lastPostIdCondition(lastPostId))
                        .orderBy(post.createdAt.desc())
                        .limit(limit)
                        .fetch();

        return getPostResultJoinWithChatRoomAndTag(limitedPostIds, memberId);
    }

    private List<PostGetResult> getPostResultJoinWithChatRoomAndTag(
            List<Long> limitedPostIds, Long memberId) {
        return queryFactory
                .from(post)
                .leftJoin(post.postTags, postTag)
                .leftJoin(postTag.tag, tag)
                .leftJoin(chatRoom)
                .on(post.id.eq(chatRoom.post.id))
                .where(post.id.in(limitedPostIds))
                .orderBy(post.createdAt.desc())
                .transform(
                        groupBy(post.id)
                                .list(
                                        new QPostGetResponse_PostGetResult(
                                                post,
                                                likeCount(),
                                                chatRoom.id,
                                                list(tag.name),
                                                isLike(memberId))));
    }

    private Expression<Long> likeCount() {
        return ExpressionUtils.as(
                JPAExpressions.select(likes.count()).from(likes).where(likes.post.id.eq(post.id)),
                "likeCount");
    }

    private Expression<Boolean> isLike(Long memberId) {
        if (memberId == null) {
            return Expressions.FALSE;
        }
        return ExpressionUtils.as(
                JPAExpressions.selectOne()
                        .from(likes)
                        .where(likes.post.id.eq(post.id), likes.member.id.eq(memberId))
                        .exists(),
                "isLike");
    }

    private BooleanExpression recruitmentStatusCondition(RecruitmentStatus recruitmentStatus) {
        if (recruitmentStatus == RecruitmentStatus.NONE) {
            return null;
        }
        return post.recruitmentStatus.eq(recruitmentStatus);
    }

    private BooleanExpression lastPostIdCondition(Long lastPostId) {
        if (lastPostId == null) return null;

        LocalDateTime lastCreatedAt =
                queryFactory
                        .select(post.createdAt)
                        .from(post)
                        .where(post.id.eq(lastPostId))
                        .fetchOne();

        return lastCreatedAt != null ? post.createdAt.lt(lastCreatedAt) : null;
    }

    private BooleanExpression followingCondition(Long memberId, Boolean isFollowing) {
        if (Boolean.TRUE.equals(isFollowing) && memberId != null) {
            return post.member.id.in(
                    queryFactory
                            .select(follow.following.id)
                            .from(follow)
                            .where(follow.follower.id.eq(memberId)));
        }

        return null;
    }

    private BooleanExpression tagsCondition(List<String> tags) {
        if (CollectionUtils.isEmpty(tags)) return null;
        BooleanExpression condition = Expressions.asBoolean(true).isTrue();
        for (String tagName : tags) {
            condition =
                    condition.and(
                            JPAExpressions.selectOne()
                                    .from(post.postTags, postTag)
                                    .join(postTag.tag, tag)
                                    .where(postTag.post.id.eq(post.id), tag.name.eq(tagName))
                                    .exists());
        }
        return condition;
    }
}
