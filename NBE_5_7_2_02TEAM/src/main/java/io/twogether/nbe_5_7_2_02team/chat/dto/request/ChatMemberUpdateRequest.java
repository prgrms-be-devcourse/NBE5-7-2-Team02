package io.twogether.nbe_5_7_2_02team.chat.dto.request;

import io.twogether.nbe_5_7_2_02team.chat.domain.ChatMemberStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatMemberUpdateRequest {
    private final ChatMemberStatus chatMemberStatus;
}
