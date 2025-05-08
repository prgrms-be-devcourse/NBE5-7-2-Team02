package io.twogether.nbe_5_7_2_02team.chat.domain;

import io.twogether.nbe_5_7_2_02team.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_member_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatroom;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime joinedAt = LocalDateTime.now();
    private Status status;

    @Builder
    public ChatMember(ChatRoom chatroom, Member member, Status status) {
        this.chatroom = chatroom;
        this.member = member;
        this.status = status;
    }
}
