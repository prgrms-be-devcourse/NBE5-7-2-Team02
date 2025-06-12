package io.twogether.nbe_5_7_2_02team.post.domain;

import io.twogether.nbe_5_7_2_02team.global.common.BaseEntity;
import io.twogether.nbe_5_7_2_02team.member.domain.Member;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Setter
    @Enumerated(EnumType.STRING)
    private RecruitmentStatus recruitmentStatus;

    @Setter private LocalDate recruitmentDeadline;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitmentField> recruitmentFields = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> postTags = new ArrayList<>();

    @Setter
    @ElementCollection
    @CollectionTable(name = "post_image", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image_url")
    @OrderColumn(name = "image_order") // 이미지 순서
    private List<String> imageUrls = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Post(
            String title,
            String content,
            RecruitmentStatus recruitmentStatus,
            List<String> imageUrls,
            Member member) {
        this.title = title;
        this.content = content;
        this.recruitmentStatus = recruitmentStatus;
        if (imageUrls != null) {
            this.imageUrls = imageUrls;
        }
        this.member = member;
    }

    public void update(String title, String content, RecruitmentStatus status) {
        if (title != null) this.title = title;
        if (content != null) this.content = content;
        if (status != null) this.recruitmentStatus = status;
    }
}
