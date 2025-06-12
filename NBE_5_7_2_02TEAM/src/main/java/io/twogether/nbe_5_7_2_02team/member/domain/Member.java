package io.twogether.nbe_5_7_2_02team.member.domain;

import io.twogether.nbe_5_7_2_02team.global.common.BaseEntity;

import jakarta.persistence.*;

import lombok.*;

import org.springframework.util.StringUtils;

import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Setter private String name;

    private String profileImage;

    @Column(nullable = false)
    private String githubId;

    @Setter private String job;

    @Setter private String course;

    @Enumerated(EnumType.STRING)
    private Role role;

    public void updateProfile(String nickname, String profileImageUrl) {
        if (StringUtils.hasText(nickname)) {
            this.name = nickname;
        }
        if (StringUtils.hasText(profileImageUrl)) {
            this.profileImage = profileImageUrl;
        }
    }

    @Builder
    public Member(
            String email,
            String name,
            String profileImage,
            String job,
            String course,
            String githubId,
            Role role) {
        this.email = email;
        this.name = name;
        this.profileImage = profileImage;
        this.job = job;
        this.course = course;
        this.githubId = githubId;
        this.role = role;
    }
}
