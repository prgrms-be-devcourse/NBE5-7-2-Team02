package io.twogether.nbe_5_7_2_02team.post.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PostImage {

    @Id
    @Column(name = "post_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public PostImage(String url, Post post) {
        this.url = url;
        this.post = post;
    }
}
