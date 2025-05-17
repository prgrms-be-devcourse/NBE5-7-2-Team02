package io.twogether.nbe_5_7_2_02team.chat.dto;

import io.twogether.nbe_5_7_2_02team.chat.domain.chatMemberStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatMemberUpdateRequest {
    private final chatMemberStatus chatMemberStatus;
}
