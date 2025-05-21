package io.twogether.nbe_5_7_2_02team.chat.dto.response;

import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ChatMessageGetResponse {

    private final Long id;
    private final Long memberId;
    private final String memberName;
    private final String content;
    private final LocalDateTime createdAt;

    public static ChatMessageGetResponse from(ChatMessage chatMessage) {
        return new ChatMessageGetResponse(
                chatMessage.getId(),
                chatMessage.getChatMember().getMember().getId(),
                chatMessage.getChatMember().getMember().getName(),
                chatMessage.getContent(),
                chatMessage.getCreatedAt());
    }
}
