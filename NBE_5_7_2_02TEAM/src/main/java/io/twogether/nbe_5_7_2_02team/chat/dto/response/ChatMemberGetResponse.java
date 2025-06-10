package io.twogether.nbe_5_7_2_02team.chat.dto.response;

import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMember;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMemberStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatMemberGetResponse {

    private final Long memberId;
    private final String memberName;
    private final String memberImage;
    private final ChatMemberStatus chatMemberStatus;

    public static ChatMemberGetResponse from(ChatMember chatMember) {
        return ChatMemberGetResponse.builder()
                .memberId(chatMember.getMember().getId())
                .memberName(chatMember.getMember().getName())
                .memberImage(chatMember.getMember().getProfileImage())
                .chatMemberStatus(chatMember.getChatMemberStatus())
                .build();
    }
}
