package io.twogether.nbe_5_7_2_02team.chat.dto.response;

import io.twogether.nbe_5_7_2_02team.chat.domain.ChatRoom;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomGetResponse {

    private final Long id;
    private final Long postId;
    private final String title;
    private final Long memberCount;
    private final LocalDateTime updatedAt;

    public static ChatRoomGetResponse from(ChatRoom chatroom) {
        return ChatRoomGetResponse.builder()
                .id(chatroom.getId())
                .postId(chatroom.getPost().getId())
                .title(chatroom.getPost().getTitle())
                .memberCount(chatroom.getMemberCount())
                .updatedAt(chatroom.getUpdatedAt())
                .build();
    }
}
