package io.twogether.nbe_5_7_2_02team.chat.api;

import static org.springframework.http.HttpStatus.CREATED;

import io.twogether.nbe_5_7_2_02team.chat.dto.request.ChatMemberUpdateRequest;
import io.twogether.nbe_5_7_2_02team.chat.dto.response.ChatMemberGetResponse;
import io.twogether.nbe_5_7_2_02team.chat.dto.response.ChatRoomGetResponse;
import io.twogether.nbe_5_7_2_02team.chat.service.ChatMemberService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
public class ChatMemberController {

    private final ChatMemberService chatMemberService;

    @GetMapping("/entered")
    public ResponseEntity<List<ChatRoomGetResponse>> getChatRoomListByUser(
            @AuthenticationPrincipal UserDetails memberDetails) {

        List<ChatRoomGetResponse> ChatRoomGetResponse =
                chatMemberService.getChatRoomListByUser(memberDetails);

        return ResponseEntity.ok(ChatRoomGetResponse);
    }

    @GetMapping("/{chatroomId}/member")
    public ResponseEntity<List<ChatMemberGetResponse>> getChatMemberList(
            @PathVariable("chatroomId") Long chatroomId) {
        List<ChatMemberGetResponse> chatMemberGetResponse =
                chatMemberService.getChatMember(chatroomId);

        return ResponseEntity.ok(chatMemberGetResponse);
    }

    @PostMapping("/{chatroomId}/member")
    public ResponseEntity<Long> createChatMember(
            @PathVariable("chatroomId") Long chatroomId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long chatMember = chatMemberService.createChatMember(chatroomId, userDetails);

        return ResponseEntity.status(CREATED).body(chatMember);
    }

    @PutMapping("/{chatroomId}/member")
    public ResponseEntity<Long> updateChatMember(
            @PathVariable("chatroomId") Long chatroomId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChatMemberUpdateRequest chatMemberUpdateRequest) {
        Long chatMember =
                chatMemberService.updateChatMember(
                        chatroomId, userDetails, chatMemberUpdateRequest.getChatMemberStatus());

        return ResponseEntity.ok(chatMember);
    }
}
