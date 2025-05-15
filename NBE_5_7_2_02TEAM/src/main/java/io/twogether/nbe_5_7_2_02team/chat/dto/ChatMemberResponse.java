package io.twogether.nbe_5_7_2_02team.chat.dto;

import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMember;
import io.twogether.nbe_5_7_2_02team.chat.domain.Status;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatMemberResponse {

    private final Long chatroomId;
    private final Long memberId;
    private final String memberName;
    private final String memberImage;
    private final LocalDateTime createdAt;
    private final Status status;

    @Builder
    public ChatMemberResponse(Long chatroomId, Long memberId, String memberName, String memberImage,
        LocalDateTime createdAt, Status status) {
        this.chatroomId = chatroomId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.memberImage = memberImage;
        this.createdAt = createdAt;
        this.status = status;
    }

    public static ChatMemberResponse from(ChatMember chatMember) {
        return ChatMemberResponse.builder()
            .chatroomId(chatMember.getChatRoom().getId())
            .memberId(chatMember.getMember().getId())
            .memberName(chatMember.getMember().getName())
            .memberImage(chatMember.getMember().getProfileImage())
            .createdAt(chatMember.getCreatedAt())
            .status(chatMember.getStatus())
            .build();
    }
}
