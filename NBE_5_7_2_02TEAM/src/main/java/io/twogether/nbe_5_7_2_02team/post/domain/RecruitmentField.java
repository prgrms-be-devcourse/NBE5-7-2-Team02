package io.twogether.nbe_5_7_2_02team.post.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false)
    private String fieldName;

    @Column(nullable = false)
    private int totalCount;

    @Column(nullable = false)
    private int currentCount = 0;

    @Column(nullable = false)
    private boolean closed = false;

    @Builder
    public RecruitmentField(
            Post post, String fieldName, int totalCount, int currentCount, boolean closed) {
        this.post = post;
        this.fieldName = fieldName;
        this.totalCount = totalCount;
        this.currentCount = currentCount;
        this.closed = closed;
    }

    public void close() {
        this.closed = true;
    }

    public void increaseCount() {
        this.currentCount++;
        if (this.currentCount >= this.totalCount) {
            this.closed = true;
        }
    }
}
