package io.twogether.nbe_5_7_2_02team.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatRoomResponse {
    private final Long id;
    private final Long postId;

    @Builder
    public ChatRoomResponse(Long id, Long postId) {
        this.id = id;
        this.postId = postId;
    }
}
