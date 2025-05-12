package io.twogether.nbe_5_7_2_02team.chat.api;

import io.twogether.nbe_5_7_2_02team.chat.dto.ChatRoomResponse;
import io.twogether.nbe_5_7_2_02team.chat.service.ChatRoomService;
import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;

import lombok.RequiredArgsConstructor;

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

    @GetMapping("/")
    public List<ChatRoomResponse> getChatRoomList() {
        return chatRoomService.getChatRoomList();
    }

    @PostMapping("/{postId}")
    public void createChatRoom(@PathVariable("postId") Long postId) {

        try {
            chatRoomService.createChatroom(postId);
        } catch (ErrorException e) {
            String message = e.getMessage();
        }
    }
}
