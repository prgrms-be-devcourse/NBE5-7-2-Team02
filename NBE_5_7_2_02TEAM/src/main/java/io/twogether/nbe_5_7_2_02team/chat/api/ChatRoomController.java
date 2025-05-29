package io.twogether.nbe_5_7_2_02team.chat.api;

import static org.springframework.http.HttpStatus.CREATED;

import io.twogether.nbe_5_7_2_02team.chat.dto.response.ChatRoomGetResponse;
import io.twogether.nbe_5_7_2_02team.chat.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping
    public ResponseEntity<List<ChatRoomGetResponse>> getChatRoomList() {
        List<ChatRoomGetResponse> chatRoomGetResponse = chatRoomService.getChatRoomList();

        return ResponseEntity.ok(chatRoomGetResponse);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ChatRoomGetResponse> getChatRoomByPost(
            @PathVariable("postId") Long postId) {
        ChatRoomGetResponse chatRoomGetResponse = chatRoomService.getChatRoomByPost(postId);

        return ResponseEntity.ok(chatRoomGetResponse);
    }

    @PostMapping("/{postId}")
    public ResponseEntity<Long> createChatRoom(@PathVariable("postId") Long postId) {
        Long id = chatRoomService.createChatroom(postId);

        return ResponseEntity.status(CREATED).body(id);
    }
}
