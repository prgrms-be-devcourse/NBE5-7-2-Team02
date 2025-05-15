package io.twogether.nbe_5_7_2_02team.chat.dto;

import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMessage;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatMessageResponse {

    private final Long id;
    private final Long chatMemberId;
    private final String content;
    private final LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return new ChatMessageResponse(
            chatMessage.getId(),
            chatMessage.getChatMember().getMember().getId(),
            chatMessage.getContent(),
            chatMessage.getCreatedAt());
    }
}
