package io.twogether.nbe_5_7_2_02team.chat.dto.response;

import io.twogether.nbe_5_7_2_02team.chat.domain.ChatRoom;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomGetResponse {

    private final Long id;
    private final Long postId;
    private final String title;

    public static ChatRoomGetResponse from(ChatRoom chatroom) {
        return ChatRoomGetResponse.builder()
                .id(chatroom.getId())
                .postId(chatroom.getPost().getId())
                .title(chatroom.getPost().getTitle())
                .build();
    }
}
