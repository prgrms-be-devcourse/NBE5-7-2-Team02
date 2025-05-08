package io.twogether.nbe_5_7_2_02team.member.domain;

import io.twogether.nbe_5_7_2_02team.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follower extends BaseEntity {

    @Id
    @Column(name = "follower_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "follower_member_id")
    private Long followerMemberId;

    @Builder
    public Follower(Member member, Long followerMemberId) {
        this.member = member;
        this.followerMemberId = followerMemberId;
    }

}
