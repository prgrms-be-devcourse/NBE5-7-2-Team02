package io.twogether.nbe_5_7_2_02team.chat.dto.response;

import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMember;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMemberStatus;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMemberGetResponse {

    private final Long chatroomId;
    private final Long memberId;
    private final String memberName;
    private final String memberImage;
    private final LocalDateTime createdAt;
    private final ChatMemberStatus chatMemberStatus;

    public static ChatMemberGetResponse from(ChatMember chatMember) {
        return ChatMemberGetResponse.builder()
                .chatroomId(chatMember.getChatRoom().getId())
                .memberId(chatMember.getMember().getId())
                .memberName(chatMember.getMember().getName())
                .memberImage(chatMember.getMember().getProfileImage())
                .createdAt(chatMember.getCreatedAt())
                .chatMemberStatus(chatMember.getChatMemberStatus())
                .build();
    }
}
