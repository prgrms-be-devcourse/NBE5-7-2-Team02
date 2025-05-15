package io.twogether.nbe_5_7_2_02team.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatRoomResponse {
    private final Long id;
    private final Long postId;
    private final String title;

    @Builder
    public ChatRoomResponse(Long id, Long postId, String title) {
        this.id = id;
        this.postId = postId;
        this.title = title;
    }
}
