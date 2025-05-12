package io.twogether.nbe_5_7_2_02team.member.domain;

import jakarta.persistence.*;

import lombok.*;

import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String githubId;

    @Column(nullable = false)
    private String name;

    private String profileImage;

    @Column(nullable = false)
    private String job;

    @Column(nullable = false)
    private String course;

    @Builder
    public Member(String githubId, String name, String profileImage, String job, String course) {
        this.githubId = githubId;
        this.name = name;
        this.profileImage = profileImage;
        this.job = job;
        this.course = course;
    }
}
