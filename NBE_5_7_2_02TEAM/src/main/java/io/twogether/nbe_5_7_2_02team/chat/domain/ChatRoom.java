package io.twogether.nbe_5_7_2_02team.chat.domain;

import io.twogether.nbe_5_7_2_02team.global.common.BaseEntity;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "member_count")
    private Long memberCount = 0L;

    @Column(name = "last_chat_id")
    private Long lastChatId = 0L;

    @Builder(toBuilder = true)
    public ChatRoom(Post post) {
        this.post = post;
    }
}
