package io.twogether.nbe_5_7_2_02team.chat.dao.adapter;

import io.twogether.nbe_5_7_2_02team.chat.dao.ChatMemberRepository;
import io.twogether.nbe_5_7_2_02team.chat.dao.ChatMessageRepository;
import io.twogether.nbe_5_7_2_02team.chat.dao.ChatRepository;
import io.twogether.nbe_5_7_2_02team.chat.dao.ChatRoomRepository;
import io.twogether.nbe_5_7_2_02team.post.domain.Post;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRepositoryAdapter implements ChatRepository {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public void deleteByPost(Post post) {
        chatRoomRepository
                .findByPost(post)
                .ifPresent(
                        chatRoom -> {
                            chatMessageRepository.deleteByChatRoom(chatRoom);
                            chatMemberRepository.deleteByChatRoom(chatRoom);
                            chatRoomRepository.delete(chatRoom);
                        });
    }
}
