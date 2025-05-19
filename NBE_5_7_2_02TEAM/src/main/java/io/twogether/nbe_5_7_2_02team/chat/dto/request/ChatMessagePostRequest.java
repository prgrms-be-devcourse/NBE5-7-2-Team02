package io.twogether.nbe_5_7_2_02team.chat.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatMessagePostRequest {
    private final Long memberId;
    private final String content;
}
