package io.twogether.nbe_5_7_2_02team.chat.dto;

import io.twogether.nbe_5_7_2_02team.chat.domain.ChatRoom;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomResponse {

    private final Long id;
    private final Long postId;
    private final String title;

    public ChatRoomResponse(Long id, Long postId, String title) {
        this.id = id;
        this.postId = postId;
        this.title = title;
    }

    public static ChatRoomResponse from(ChatRoom chatroom) {
        return ChatRoomResponse.builder()
                .id(chatroom.getId())
                .postId(chatroom.getPost().getId())
                .title(chatroom.getPost().getTitle())
                .build();
    }
}
