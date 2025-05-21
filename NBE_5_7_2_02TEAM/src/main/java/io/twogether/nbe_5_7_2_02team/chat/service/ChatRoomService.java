package io.twogether.nbe_5_7_2_02team.chat.service;

import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_ROOM_ALREADY_EXISTS;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_ROOM_NOT_FOUND;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.NOT_FOUND_POST;

import io.twogether.nbe_5_7_2_02team.chat.dao.ChatRoomRepository;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatRoom;
import io.twogether.nbe_5_7_2_02team.chat.dto.response.ChatRoomGetResponse;
import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.post.dao.PostRepository;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public List<ChatRoomGetResponse> getChatRoomList() {
        List<ChatRoom> chatRoomList = chatRoomRepository.findAll();

        return chatRoomList.stream().map(ChatRoomGetResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ChatRoomGetResponse getChatRoomByPost(Long postId) {
        Post post =
                postRepository
                        .findById(postId)
                        .orElseThrow(() -> new ErrorException(NOT_FOUND_POST));

        ChatRoom chatRoom =
                chatRoomRepository
                        .findByPost(post)
                        .orElseThrow(() -> new ErrorException(CHAT_ROOM_NOT_FOUND));

        return ChatRoomGetResponse.from(chatRoom);
    }

    @Transactional
    public Long createChatroom(Long postId) {
        Post post =
                postRepository
                        .findById(postId)
                        .orElseThrow(() -> new ErrorException(NOT_FOUND_POST));

        chatRoomRepository
                .findByPost(post)
                .ifPresent(
                        chatRoom -> {
                            throw new ErrorException(CHAT_ROOM_ALREADY_EXISTS);
                        });

        return chatRoomRepository.save(ChatRoom.builder().post(post).build()).getId();
    }

    @Transactional
    public void deleteChatroom(Long id) {
        ChatRoom chatRoom = checkChatRoomExists(id);

        chatRoomRepository.delete(chatRoom);
    }

    public ChatRoom checkChatRoomExists(Long id) {
        return chatRoomRepository
                .findById(id)
                .orElseThrow(() -> new ErrorException(CHAT_ROOM_NOT_FOUND));
    }
}
