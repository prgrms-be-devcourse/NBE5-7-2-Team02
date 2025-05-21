package io.twogether.nbe_5_7_2_02team.chat.api;

import io.twogether.nbe_5_7_2_02team.chat.dto.request.ChatMemberUpdateRequest;
import io.twogether.nbe_5_7_2_02team.chat.dto.response.ChatMemberGetResponse;
import io.twogether.nbe_5_7_2_02team.chat.dto.response.ChatRoomGetResponse;
import io.twogether.nbe_5_7_2_02team.chat.service.ChatMemberService;
import io.twogether.nbe_5_7_2_02team.global.response.success.BaseResponse;
import io.twogether.nbe_5_7_2_02team.global.response.success.SuccessCode;

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

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
public class ChatMemberController {

    private final ChatMemberService chatMemberService;

    @GetMapping("/entered")
    public ResponseEntity<BaseResponse<List<ChatRoomGetResponse>>> getChatRoomListByUser(
            @AuthenticationPrincipal UserDetails memberDetails) {

        List<ChatRoomGetResponse> ChatRoomGetResponse = chatMemberService.getChatRoomListByUser(memberDetails);

        return BaseResponse.of(SuccessCode.FOUND_CHAT_MEMBER, ChatRoomGetResponse, null);
    }

    @GetMapping("/{chatroomId}/member")
    public ResponseEntity<BaseResponse<List<ChatMemberGetResponse>>> getChatMemberList(
            @PathVariable("chatroomId") Long chatroomId) {
        List<ChatMemberGetResponse> chatMemberGetResponse =
                chatMemberService.getChatMember(chatroomId);

        return BaseResponse.of(SuccessCode.FOUND_CHAT_MEMBER, chatMemberGetResponse, null);
    }

    @PostMapping("/{chatroomId}/member")
    public ResponseEntity<BaseResponse<Long>> createChatMember(
            @PathVariable("chatroomId") Long chatroomId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long chatMember = chatMemberService.createChatMember(chatroomId, userDetails);

        return BaseResponse.of(
                SuccessCode.CREATE_CHAT_MEMBER,
                chatMember,
                URI.create("/api/chatroom/" + chatroomId + "/member"));
    }

    @PutMapping("/{chatroomId}/member")
    public ResponseEntity<BaseResponse<Long>> updateChatMember(
            @PathVariable("chatroomId") Long chatroomId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChatMemberUpdateRequest chatMemberUpdateRequest) {
        Long chatMember =
                chatMemberService.updateChatMember(
                        chatroomId, userDetails, chatMemberUpdateRequest.getChatMemberStatus());

        return BaseResponse.of(SuccessCode.UPDATE_CHAT_MEMBER, chatMember, null);
    }
}
