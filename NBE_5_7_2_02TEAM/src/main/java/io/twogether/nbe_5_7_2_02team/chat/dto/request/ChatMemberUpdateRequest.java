package io.twogether.nbe_5_7_2_02team.chat.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMemberStatus;

import lombok.Getter;

@Getter
public class ChatMemberUpdateRequest {
    private final ChatMemberStatus chatMemberStatus;

    @JsonCreator
    public ChatMemberUpdateRequest(
            @JsonProperty("chatMemberStatus") ChatMemberStatus chatMemberStatus) {
        this.chatMemberStatus = chatMemberStatus;
    }
}
