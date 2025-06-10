package io.twogether.nbe_5_7_2_02team.chat.api;

import io.twogether.nbe_5_7_2_02team.chat.dto.request.ChatMessagePostRequest;
import io.twogether.nbe_5_7_2_02team.chat.dto.response.ChatMessageGetResponse;
import io.twogether.nbe_5_7_2_02team.chat.service.ChatMessageService;
import io.twogether.nbe_5_7_2_02team.oauth.dto.common.TokenBody;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chatroom")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/{chatroomId}/message")
    public ResponseEntity<List<ChatMessageGetResponse>> getChatMessageList(
            @PathVariable Long chatroomId) {
        List<ChatMessageGetResponse> chatMessage = chatMessageService.getChatMessage(chatroomId);

        return ResponseEntity.ok(chatMessage);
    }

    @MessageMapping("/{chatroomId}/message")
    @SendTo("/sub/{chatroomId}/message")
    public ChatMessageGetResponse createChatMessage(
            @DestinationVariable Long chatroomId,
            @Payload ChatMessagePostRequest chatMessagePostRequest,
            Principal principal) {

        TokenBody tokenBody = null;
        Long memberId = null;
        String userRole = null;

        if (principal instanceof Authentication) {
            Authentication authentication = (Authentication) principal;
            Object authPrincipal = authentication.getPrincipal();

            if (authPrincipal instanceof TokenBody) {
                tokenBody = (TokenBody) authPrincipal;
                memberId = tokenBody.getMemberId();
                userRole = tokenBody.getRole().name();
            }
        }

        ChatMessageGetResponse ChatMessageGetResponse =
                chatMessageService.createChatMessage(chatroomId, chatMessagePostRequest, memberId);

        return ChatMessageGetResponse;
    }

    @DeleteMapping("/{chatroomId}/message")
    public ResponseEntity<Long> deleteChatMessage(
            @PathVariable Long chatroomId,
            @RequestParam Long chatMessageId,
            @AuthenticationPrincipal UserDetails userDetails) {
        chatMessageService.deleteChatMessage(chatMessageId, chatroomId, userDetails);

        return ResponseEntity.ok(chatroomId);
    }
}
