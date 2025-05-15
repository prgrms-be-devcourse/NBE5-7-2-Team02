package io.twogether.nbe_5_7_2_02team.chat.api;

import io.twogether.nbe_5_7_2_02team.chat.dto.ChatMessageDeleteRequest;
import io.twogether.nbe_5_7_2_02team.chat.dto.ChatMessageRequest;
import io.twogether.nbe_5_7_2_02team.chat.dto.ChatMessageResponse;
import io.twogether.nbe_5_7_2_02team.chat.service.ChatMessageService;
import io.twogether.nbe_5_7_2_02team.global.response.success.BaseResponse;
import io.twogether.nbe_5_7_2_02team.global.response.success.SuccessCode;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatroom")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/{chatroomId}/message")
    public ResponseEntity<BaseResponse<List<ChatMessageResponse>>> getChatMessageList(@PathVariable Long chatroomId){
        List<ChatMessageResponse> chatMessage = chatMessageService.getChatMessage(chatroomId);

        return BaseResponse.of(SuccessCode.FOUND_CHAT_MESSAGE, chatMessage, null);
    }

    @PostMapping("/{chatroomId}/message")
    public ResponseEntity<BaseResponse<Long>> createChatMessage(@PathVariable Long chatroomId, @RequestBody ChatMessageRequest chatMessageRequest, @AuthenticationPrincipal UserDetails userDetails){
        Long chatMessageId = chatMessageService.createChatMessage(chatroomId, chatMessageRequest,
            userDetails);

        return BaseResponse.of(SuccessCode.CREATE_CHAT_MESSAGE, chatMessageId, URI.create("/api/chatroom/" + chatroomId + "/message"));
    }

    @DeleteMapping("/{chatroomId}/message")
    public ResponseEntity<BaseResponse<Long>> deleteChatMessage(@PathVariable Long chatroomId, @RequestBody ChatMessageDeleteRequest chatMessageDeleteRequest, @AuthenticationPrincipal UserDetails userDetails){
        chatMessageService.deleteChatMessage(chatMessageDeleteRequest.getChatMessageId(), chatroomId, userDetails);

        return BaseResponse.of(SuccessCode.DELETE_CHAT_MESSAGE, chatroomId, null);
    }
}
