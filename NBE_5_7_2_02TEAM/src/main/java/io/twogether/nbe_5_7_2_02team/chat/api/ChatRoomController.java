package io.twogether.nbe_5_7_2_02team.chat.api;

import io.twogether.nbe_5_7_2_02team.chat.dto.ChatRoomGetResponse;
import io.twogether.nbe_5_7_2_02team.chat.service.ChatRoomService;
import io.twogether.nbe_5_7_2_02team.global.response.success.BaseResponse;
import io.twogether.nbe_5_7_2_02team.global.response.success.SuccessCode;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<ChatRoomGetResponse>>> getChatRoomList() {
        List<ChatRoomGetResponse> chatRoomGetResponse = chatRoomService.getChatRoomList();

        return BaseResponse.of(SuccessCode.FOUND_CHATROOM, chatRoomGetResponse, null);
    }

    @PostMapping("/{postId}")
    public ResponseEntity<BaseResponse<Long>> createChatRoom(@PathVariable("postId") Long postId) {
        Long id = chatRoomService.createChatroom(postId);

        return BaseResponse.of(SuccessCode.CREATE_CHATROOM, id, URI.create("/api/chatroom/" + id));
    }
}
