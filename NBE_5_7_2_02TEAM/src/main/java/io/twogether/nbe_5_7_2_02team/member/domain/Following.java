package io.twogether.nbe_5_7_2_02team.member.domain;

import io.twogether.nbe_5_7_2_02team.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Following extends BaseEntity {

    @Id
    @Column(name = "following_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "following_member_id")
    private Long followingMemberId;

    @Builder
    public Following(Member member, Long followingMemberId) {
        this.member = member;
        this.followingMemberId = followingMemberId;
    }

}
