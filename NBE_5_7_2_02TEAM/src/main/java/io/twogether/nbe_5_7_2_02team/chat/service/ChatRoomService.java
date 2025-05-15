package io.twogether.nbe_5_7_2_02team.chat.service;

import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_ROOM_ALREADY_EXISTS;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_ROOM_LIST_EMPTY;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.CHAT_ROOM_NOT_FOUND;
import static io.twogether.nbe_5_7_2_02team.global.response.error.ErrorCode.POST_NOT_FOUND;

import io.twogether.nbe_5_7_2_02team.chat.dao.ChatRoomRepository;
import io.twogether.nbe_5_7_2_02team.chat.domain.ChatRoom;
import io.twogether.nbe_5_7_2_02team.chat.dto.ChatRoomResponse;
import io.twogether.nbe_5_7_2_02team.global.exception.ErrorException;
import io.twogether.nbe_5_7_2_02team.post.dao.PostRepository;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final PostRepository postRepository;

    @Transactional
    public List<ChatRoomResponse> getChatRoomList() {
        List<ChatRoom> chatRoomList = chatRoomRepository.findAll();

        if (chatRoomList.isEmpty()) {
            throw new ErrorException(CHAT_ROOM_LIST_EMPTY);
        }

        return chatRoomList.stream()
                .map(
                        ChatRoom ->
                                ChatRoomResponse.builder()
                                        .id(ChatRoom.getId())
                                        .postId(ChatRoom.getPost().getId())
                                        .title(ChatRoom.getPost().getTitle())
                                        .build())
                .toList();
    }

    @Transactional
    public Long createChatroom(Long postId) {
        Post post =
                postRepository
                        .findById(postId)
                        .orElseThrow(() -> new ErrorException(POST_NOT_FOUND));

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
